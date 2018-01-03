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

import org.jigsaw.payment.core.AccountRepository;
import org.jigsaw.payment.core.PaymentSharder;
import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.Account;
import org.jigsaw.payment.model.IllegalParameterException;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月17日
 */
public class MySQLShardingAccountRepository implements AccountRepository {

	private PaymentSharder<Account> accountSharder;


	public MySQLShardingAccountRepository(
			PaymentSharder<Account> accountSharder) {
		this.accountSharder = accountSharder;
	
	}

	private static final String SQL_GET_ACCOUNT = "select * from {0} where id = ?";

	@Override
	@Timer("get-account")
	public Account get(long id) {
		JdbcProtobufTemplate<Long, Account> template = this.accountSharder
				.getTemplateById(id);
		String tableName = this.accountSharder.getTableById(id);
		String sql = MessageFormat.format(SQL_GET_ACCOUNT, tableName);
		return template.get(sql, id);
	}

	/**
	 * 验证数据有效性
	 * 
	 * @param account
	 */
	private void validate(Account account) {
		if (!account.hasId())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.account.require.id");
		if (!account.hasKey())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.account.require.key");
		if (!account.hasCreateTime())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.account.require.create_time");

	}

	@Override
	@Timer("create-account")
	public long create(Account account) {
		this.validate(account);
		String tableName = this.accountSharder.getTableById(account.getId());
		this.accountSharder.getTemplateById(account.getId()).insert(account,
				tableName);
		return account.getId();
	}

	
}
