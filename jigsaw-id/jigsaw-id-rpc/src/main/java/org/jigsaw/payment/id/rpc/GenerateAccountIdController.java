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
package org.jigsaw.payment.id.rpc;

import javax.annotation.PostConstruct;

import org.jigsaw.payment.algorithm.LUHN;
import org.jigsaw.payment.id.RedisTemplate;
import org.jigsaw.payment.id.ShardingByUserId;
import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.AccountTitle;
import org.jigsaw.payment.model.AccountType;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.rpc.IdService.GenerateAccountIdRequest;
import org.jigsaw.payment.rpc.IdService.GenerateAccountIdResponse;
import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;
import org.jigsaw.payment.rpc.server.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月22日
 */
@Component("generateAccountId")
public class GenerateAccountIdController implements
		Controller<GenerateAccountIdRequest, GenerateAccountIdResponse> {

	private static final Logger logger = LoggerFactory
			.getLogger(GenerateAccountIdController.class);

	/**
	 * 账户表主键的redis key，产生递增主键
	 */
	private static final String REDIS_ACCOUNT_KEY_KEY = "pay-account-key";
	private static final String REDIS_ACCOUNT_ID_KEY = "pay-account-id";
	private static final String REDIS_ACCOUNT_CONTRACT_KEY_KEY = "pay-account-contract-key";

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private ShardingByUserId shardingConfig;
	// 序列号最大值
	private int maxSequence;

	@PostConstruct
	public void init() {
		this.maxSequence = 10 ^ 12 / this.shardingConfig
				.getMaxShardingDatabaseCount();
	}

	@Override
	@Timer("generateAccountId")
	public GenerateAccountIdResponse process(GenerateAccountIdRequest request)
			throws NotFoundException, SystemException, UserException {
		if (request.getSubId() < 1)
			throw new UserException().setErrorCode(
					StatusCode.BAD_DATA_FORMAT_VALUE).setMessage(
					"id.service.sub_id.error");

		GenerateAccountIdResponse.Builder builder = GenerateAccountIdResponse
				.newBuilder();
		long key = generateKey(request.getSubId());
		builder.setAccountKey(key);
		long id = generateId(request.getAccountTitle(),
				request.getAccountType(), request.getSubId());
		builder.setAccountId(id);
		logger.debug(
				"generate account_key={} , account_id={}, contract_key={} for sub_id={}.",
				key, id, request.getSubId());
		return builder.build();
	}

	/**
	 * 账户号id算法：
	 * <ul>
	 * <li>1~4位：二级会计科目，暂定2203</li>
	 * <li>5位: 账户类型，对公，对私或者内部</li>
	 * <li>6-17位：确保在最多支持128个库的情况下，订单号能够和uid被sharding到同一个库中，同时兼容1、2、4、8、16、32、64
	 * 个库的场景。</li>
	 * <li>18位: uid % 10 , 确保订单号和uid能够被sharding到同一张表中。</li>
	 * <li>19位: LUHN校验算法产生的校验码。</li>
	 * </ul>
	 * 递增序列号必须小于10^7/128，即78125。也就是在极端情况下，每秒支持在同一个表中产生78125个订单。
	 * 递增序列号使用redis来生成，key是 pay-order-{uid%128}-{uid%10}。
	 * 
	 * @param uid
	 * @return
	 */
	private long generateId(AccountTitle title, AccountType type,
			long uid) {
		StringBuilder builder = new StringBuilder();
		builder.append(title.getNumber() % 10000);
		builder.append(type.getNumber());
		long databaseIndex = this.shardingConfig.getDatabaseIndex(uid);
		long tableIndex = this.shardingConfig.getTableIndex(uid);
		long sequence = redisTemplate.incr(REDIS_ACCOUNT_ID_KEY);
		if (sequence >= this.maxSequence) {
			redisTemplate.del(REDIS_ACCOUNT_ID_KEY);
			sequence = 1;
		}
		sequence = sequence * this.shardingConfig.getMaxShardingDatabaseCount()
				+ databaseIndex;
		builder.append(String.format("%012d", sequence));
		builder.append(tableIndex);
		builder.append(LUHN.gen(builder.toString()));
		return Long.parseLong(builder.toString());
	}

	/**
	 * 生成订单ID，需要保证全局唯一
	 * 
	 * @param uid
	 * @return
	 */
	protected long generateKey(long uid) {
		return redisTemplate.incr(REDIS_ACCOUNT_KEY_KEY);
	}

	/**
	 * 生成订单ID，需要保证全局唯一
	 * 
	 * @param uid
	 * @return
	 */
	protected long generateContractKey(long uid) {
		return redisTemplate.incr(REDIS_ACCOUNT_CONTRACT_KEY_KEY);
	}
}
