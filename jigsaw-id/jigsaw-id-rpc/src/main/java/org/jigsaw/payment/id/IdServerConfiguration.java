package org.jigsaw.payment.id;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月16日
 */
@Configuration
@ComponentScan("org.jigsaw.payment.id.rpc")
public class IdServerConfiguration {

	@Value("${spring.redis.pool.maxIdle}")
	private int idle = 10;
	@Value("${spring.redis.pool.maxActive}")
	private int total = 100;
	@Value("${spring.redis.pool.maxWaitMillis}")
	private long wait = 1000;
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.password}")
	private String password;
	@Value("${spring.redis.so.timeout}")
	private int soTimeout = 1000;
	@Value("${spring.redis.connect.timeout}")
	private int connectTimeout = 1000;

	@Bean
	public ShardedJedisPool shardedJedisPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(idle);
		jedisPoolConfig.setMaxTotal(total);
		jedisPoolConfig.setMaxWaitMillis(wait);

		List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
		String[] hosts = host.split(",");
		for (String node : hosts) {
			String nodeStr[] = node.split(":");
			JedisShardInfo jedisShardInfo = new JedisShardInfo(nodeStr[0],
					Integer.valueOf(nodeStr[1]));
			// for test/prod
			if (StringUtils.isNotBlank(password))
				jedisShardInfo.setPassword(password);
			// timeout will discard message
			jedisShardInfo.setSoTimeout(soTimeout);
			jedisShardInfo.setConnectionTimeout(connectTimeout);
			jedisShardInfos.add(jedisShardInfo);
		}

		ShardedJedisPool shardedJedisPool = new ShardedJedisPool(
				jedisPoolConfig, jedisShardInfos);
		return shardedJedisPool;
	}
	
	@Bean
	@Autowired
	public RedisTemplate redisTemplate(ShardedJedisPool shardedJedisPool){
		return new RedisTemplate(shardedJedisPool);
	}
	
	@Bean
	public ShardingByUserId shardingByUserId(){
		return ShardingByUserId.newBuilder().build();
	}

}
