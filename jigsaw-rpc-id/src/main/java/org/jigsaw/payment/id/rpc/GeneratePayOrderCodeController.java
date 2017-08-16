package org.jigsaw.payment.id.rpc;

import org.jigsaw.payment.algorithm.LUHN;
import org.jigsaw.payment.id.RedisTemplate;
import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.rpc.IdService.GeneratePayOrderCodeRequest;
import org.jigsaw.payment.rpc.IdService.GeneratePayOrderCodeResponse;
import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;
import org.jigsaw.payment.rpc.server.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("generatePayOrderCode")
public class GeneratePayOrderCodeController implements
		Controller<GeneratePayOrderCodeRequest, GeneratePayOrderCodeResponse> {
	private static final Logger logger = LoggerFactory
			.getLogger(GeneratePayOrderCodeController.class);
	// 数据库分片的最大数；
	private static final int MAX_SHARDING_DATABASE_COUNT = 128;
	// 序列号最大值
	private static final int MAX_SEQUENCE = 10 ^ 7 / MAX_SHARDING_DATABASE_COUNT;
	// 每个分片的表个数
	private static final int SHARDING_TABLE_COUNT = 10;

	private static final String REDIS_PAY_ORDER_ID_KEY = "pay-order-id";

	private RedisTemplate redisTemplate;

	@Autowired
	public GeneratePayOrderCodeController(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	@Timer("generatePayOrderCode")
	public GeneratePayOrderCodeResponse process(
			GeneratePayOrderCodeRequest request) throws NotFoundException,
			SystemException, UserException {
		if (request.getSubId() < 1)
			throw new UserException().setErrorCode(
					StatusCode.BAD_DATA_FORMAT_VALUE).setMessage(
					"id.service.sub_id.error");

		GeneratePayOrderCodeResponse.Builder builder = GeneratePayOrderCodeResponse
				.newBuilder();
		long id = generateId(request.getSubId());
		builder.setId(id);
		String code = generateCode(request.getSubId());
		builder.setCode(code);
		logger.debug("generate id={} and code={} for sub_id={}.", id, code,
				request.getSubId());
		return builder.build();
	}

	/**
	 * 订单号PayOrder算法：
	 * <ul>
	 * <li>1~10： 当前时间转换成秒，如2017-12-20 12:50:32， 转化成 1513745432</li>
	 * <li>11~17: 递增序列号 * 128 + uid /10 % 128
	 * 确保在最多支持128个库的情况下，订单号能够和uid被sharding到同一个库中，同时兼容1、2、4、8、16、32、64个库的场景。</li>
	 * <li>18: uid % 10 , 确保订单号和uid能够被sharding到同一张表中。</li>
	 * <li>19: LUHN校验算法产生的校验码。</li>
	 * </ul>
	 * 递增序列号必须小于10^7/128，即78125。也就是在极端情况下，每秒支持在同一个表中产生78125个订单。
	 * 递增序列号使用redis来生成，key是 pay-order-{uid%128}-{uid%10}。
	 * 
	 * @param uid
	 * @return
	 */
	protected String generateCode(long uid) {
		StringBuilder builder = new StringBuilder();
		builder.append(System.currentTimeMillis() / 1000);

		long databaseIndex = uid / SHARDING_TABLE_COUNT
				% MAX_SHARDING_DATABASE_COUNT;
		long tableIndex = uid % SHARDING_TABLE_COUNT;
		String redisKey = "pay-order-" + databaseIndex + "-" + tableIndex;
		long sequence = redisTemplate.incr(redisKey);
		if (sequence >= MAX_SEQUENCE) {
			redisTemplate.del(redisKey);
			sequence = 0;
		}
		sequence = sequence * MAX_SHARDING_DATABASE_COUNT + uid
				/ SHARDING_TABLE_COUNT % MAX_SHARDING_DATABASE_COUNT;
		builder.append(String.format("%07d", sequence));
		builder.append(uid % SHARDING_TABLE_COUNT);
		builder.append(LUHN.gen(builder.toString()));
		return builder.toString();
	}

	/**
	 * 生成订单ID，需要保证全局唯一
	 * 
	 * @param uid
	 * @return
	 */
	protected long generateId(long uid) {
		return redisTemplate.incr(REDIS_PAY_ORDER_ID_KEY);
	}
}
