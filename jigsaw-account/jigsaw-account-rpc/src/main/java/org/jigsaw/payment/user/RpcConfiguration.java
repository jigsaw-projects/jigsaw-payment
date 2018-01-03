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
package org.jigsaw.payment.user;

import javax.sql.DataSource;

import org.jigsaw.payment.model.Account;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;
import org.jigsaw.payment.user.mysql.MySQLAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 运行user rpc service所需要的配置。注意这里使用的是自动配置。
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年9月8日
 */
@Configuration
public class RpcConfiguration {
	
	@Bean
	@Autowired
	public JdbcProtobufTemplate<String, Account> userTemplate(DataSource datasource){
		JdbcTemplate template = new JdbcTemplate(datasource);
		return new JdbcProtobufTemplate<String, Account>(template, Account.class, "entity_account");
	}
	
	@Bean
	@Autowired
	public AccountRepository userRepository(JdbcProtobufTemplate<String, Account> template){
		return new MySQLAccountRepository(template);
	}

}
