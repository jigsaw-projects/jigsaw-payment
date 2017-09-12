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
package org.jigsaw.payment.order;

import org.jigsaw.payment.core.AccountRepository;
import org.jigsaw.payment.core.ContractAccountRepository;
import org.jigsaw.payment.core.PaymentSharder;
import org.jigsaw.payment.core.mysql.MySQLShardingAccountRepository;
import org.jigsaw.payment.core.mysql.MySQLShardingContractAccountRepository;
import org.jigsaw.payment.core.mysql.MySQLShardingPayOrderRepository;
import org.jigsaw.payment.model.Account;
import org.jigsaw.payment.model.ContractAccount;
import org.jigsaw.payment.model.PayOrder;
import org.jigsaw.payment.rpc.server.RpcServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月14日
 */

@Configuration
@ImportResource("classpath:datasource.xml")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,RpcServerConfiguration.class})
public class TestConfiguration {

	
	@Bean("accountRepository")
	@Autowired
	public AccountRepository accountRepository(@Qualifier("accountSharder")PaymentSharder<Account> accountSharder){
		return new MySQLShardingAccountRepository(accountSharder);
	}
	
	@Bean("contractAccountRepository")
	@Autowired
	public ContractAccountRepository contractAccountRepository(@Qualifier("contractSharder")PaymentSharder<ContractAccount> contractSharder){
		return new MySQLShardingContractAccountRepository(contractSharder);
	}
	
	@Bean("payOrderRepository")
	@Autowired
	public MySQLShardingPayOrderRepository payOrderRepository(@Qualifier("payOrderSharder")PaymentSharder<PayOrder> jdbcSharder){
		return new MySQLShardingPayOrderRepository(jdbcSharder);
	}
	
}
