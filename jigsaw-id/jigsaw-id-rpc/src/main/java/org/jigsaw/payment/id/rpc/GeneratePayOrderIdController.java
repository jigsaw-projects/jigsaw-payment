package org.jigsaw.payment.id.rpc;

import javax.annotation.PostConstruct;

import org.jigsaw.payment.algorithm.LUHN;
import org.jigsaw.payment.id.RedisTemplate;
import org.jigsaw.payment.id.ShardingByUserId;
import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.rpc.IdService.GeneratePayOrderIdRequest;
import org.jigsaw.payment.rpc.IdService.GeneratePayOrderIdResponse;
import org.jigsaw.payment.rpc.NotFoundException;
import org.jigsaw.payment.rpc.SystemException;
import org.jigsaw.payment.rpc.UserException;
import org.jigsaw.payment.rpc.server.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生成创建订单的Id和Key。 Id可以支持2~128个数据库（2为模）X 10张表的分表分库策略， 并且在增加分库（比如从2增加到4增加到8等）时，已有的订单id仍然可以支持按照用户uid进行分片的需求。
 * 确保每次产生的Key可以在跨表跨库中递增。 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月22日
 */
@Component("generatePayOrderId")
public class GeneratePayOrderIdController implements
		Controller<GeneratePayOrderIdRequest, GeneratePayOrderIdResponse> {
	private static final Logger logger = LoggerFactory
			.getLogger(GeneratePayOrderIdController.class);


	private static final String REDIS_PAY_ORDER_KEY_KEY = "pay-order-key";

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private ShardingByUserId shardingConfig;
	// 序列号最大值
	private int maxSequence ;
	
	
	@PostConstruct
	public void init(){
		this.maxSequence = 10 ^ 7 / this.shardingConfig.getMaxShardingDatabaseCount();
	}

	@Override
	@Timer("generatePayOrderId")
	public GeneratePayOrderIdResponse process(
			GeneratePayOrderIdRequest request) throws NotFoundException,
			SystemException, UserException {
		if (request.getSubId() < 1)
			throw new UserException().setErrorCode(
					StatusCode.BAD_DATA_FORMAT_VALUE).setMessage(
					"id.service.sub_id.error");

		GeneratePayOrderIdResponse.Builder builder = GeneratePayOrderIdResponse
				.newBuilder();
		long key = generateKey(request.getSubId());
		builder.setKey(key);
		long id = generateId(request.getSubId());
		builder.setId(id);
		logger.debug("generate key={} and id={} for sub_id={}.", key, id,
				request.getSubId());
		return builder.build();
	}

	/**
	 * 订单号PayOrder算法：
	 * <ul>
	 * <li>1~10位： 当前时间转换成秒，如2017-12-20 12:50:32， 转化成 1513745432</li>
	 * <li>11~17位: 递增序列号 * 128 + uid /10 % 128
	 * 确保在最多支持128个库的情况下，订单号能够和uid被sharding到同一个库中，同时兼容1、2、4、8、16、32、64个库的场景。</li>
	 * <li>18位: uid % 10 , 确保订单号和uid能够被sharding到同一张表中。</li>
	 * <li>19位: LUHN校验算法产生的校验码。</li>
	 * </ul>
	 * 递增序列号必须小于10^12/128，即78125。也就是在极端情况下，每秒支持在同一个表中产生78125个订单。
	 * 递增序列号使用redis来生成，key是 pay-order-{uid%128}-{uid%10}。
	 * 
	 * @param uid
	 * @return
	 */
	private long generateId(long uid) {
		StringBuilder builder = new StringBuilder();
		builder.append(System.currentTimeMillis() / 1000);

		long databaseIndex = this.shardingConfig.getDatabaseIndex(uid);
		long tableIndex = this.shardingConfig.getTableIndex(uid);
		String redisKey = "pay-order-" + databaseIndex + "-" + tableIndex;
		long sequence = redisTemplate.incr(redisKey);
		if (sequence >= this.maxSequence) {
			redisTemplate.del(redisKey);
			sequence = 0;
		}
		sequence = sequence * this.shardingConfig.getMaxShardingDatabaseCount() + databaseIndex;
		builder.append(String.format("%07d", sequence));
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
		return redisTemplate.incr(REDIS_PAY_ORDER_KEY_KEY);
	}
}
