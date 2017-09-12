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

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomUtils;
import org.jigsaw.payment.core.AccountRepository;
import org.jigsaw.payment.model.Account;
import org.jigsaw.payment.model.Account.AccountNotification;
import org.jigsaw.payment.model.Account.AccountPermission;
import org.jigsaw.payment.model.FeeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月21日
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes= TestConfiguration.class, webEnvironment=WebEnvironment.NONE)
public class TestAccountRepository {

	@Resource(name = "accountRepository")
	private AccountRepository accountRepository;
	
	@Test
	public void testCreateAndGet(){
		Account.Builder builder = Account.newBuilder();
		long uid = System.currentTimeMillis();
		long id = (uid * 10 + 1);
		builder.setId(id);
		builder.setBalance(RandomUtils.nextInt(0, 1000));
		builder.setCreateTime(System.currentTimeMillis());
		builder.setFeeUnit(FeeUnit.CNY);
		builder.setFrozen(RandomUtils.nextInt(0, 100));
		builder.setIncome(RandomUtils.nextInt(0, 1000));
		builder.setSandbox(false);
		builder.setKey(System.currentTimeMillis());
		builder.setNotification(AccountNotification.EMAIL);
		builder.setPermissions(AccountPermission.ALLOW_IN_VALUE | AccountPermission.ALLOW_OUT_VALUE);
		builder.setRank(1);
		builder.setRiskLevel(Account.RiskLevel.HIGH);
		this.accountRepository.create(builder.build());
		
		
	}
}
