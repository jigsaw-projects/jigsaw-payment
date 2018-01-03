/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jigsaw.payment.core.mysql;

import java.text.MessageFormat;

import org.jigsaw.payment.core.ContractAccountRepository;
import org.jigsaw.payment.core.PaymentSharder;
import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.ContractAccount;
import org.jigsaw.payment.model.IllegalParameterException;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月24日
 */
public class MySQLShardingContractAccountRepository implements
		ContractAccountRepository {
	private PaymentSharder<ContractAccount> contractSharder;

	public MySQLShardingContractAccountRepository(
			PaymentSharder<ContractAccount> contractSharder) {
		this.contractSharder = contractSharder;
	}

	/**
	 * 验证数据有效性
	 * 
	 * @param account
	 */
	private void validate(ContractAccount contract) {
		if (!contract.hasId())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.contract.require.id");
		if (!contract.hasContractAccount())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.contract.required.contract_account");
		if (!contract.hasCreateTime())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.contract.require.create_time");

	}

	@Override
	@Timer("create-contract-account")
	public long create(ContractAccount contract) {
		this.validate(contract);

		String tableName = this.contractSharder.getTableById(contract.getId());
		this.contractSharder.getTemplateById(contract.getId()).insert(contract,
				tableName);
		return contract.getId();
	}

	private static final String SQL_GET_ACCOUNT = "select * from {0} where id = ?";

	@Override
	@Timer("get-contract-account")
	public ContractAccount get(long id) {
		JdbcProtobufTemplate<Long, ContractAccount> template = this.contractSharder
				.getTemplateById(id);
		String tableName = this.contractSharder.getTableById(id);
		String sql = MessageFormat.format(SQL_GET_ACCOUNT, tableName);
		return template.get(sql, id);
	}

}
