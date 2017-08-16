package com.iqiyi.pay.frontend.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by leishengbao on 8/25/16.
 */
@Component
public class BankInfoCacheManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(BankInfoCacheManager.class);


    @Resource(name = "mapRedisTemplate")
    RedisTemplate redisTemplate;

    @Resource(name = "stringRedisTemplate")
    RedisTemplate stringRedisTemplate;

    public static final String CONTRACT_INFO = "order_contract_info_";



    public String setBankTransInfo(Map<String, String> cardInfo){
        String uid = cardInfo.get("user_id");
        cardInfo.put("uid", uid);
        String key = String.valueOf(System.currentTimeMillis()) + uid;
        ValueOperations<String, Map<String, String>> ops = redisTemplate.opsForValue();
        ops.set(key, cardInfo);
        redisTemplate.expire(key, 3000, TimeUnit.SECONDS);
        return key;
    }



    public Map<String, String> getBankTransInfo(String cacheKey){
        ValueOperations<String, Map<String, String>> ops = redisTemplate.opsForValue();
        Map<String, String> cardInfo = ops.get(cacheKey);
        return cardInfo;
    }


    public void setOrderContractInfo(String orderCode, String isContract){
        String key = CONTRACT_INFO + orderCode;
        LOGGER.info("[set][key:{}][isContract:{}]", key, isContract);
        ValueOperations ops = stringRedisTemplate.opsForValue();
        ops.set(key, isContract);
        stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);
    }


    public String getOrderContractInfo(String orderCode){
        String key = CONTRACT_INFO + orderCode;
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String isContract = operations.get(key);
        LOGGER.info("[get][key:{}][isContract:{}]", key, isContract);
        return isContract;
    }
}
