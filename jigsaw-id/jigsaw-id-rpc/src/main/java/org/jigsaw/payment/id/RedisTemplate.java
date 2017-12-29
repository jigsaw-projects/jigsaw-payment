package org.jigsaw.payment.id;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月16日
 */
public class RedisTemplate {

   
    private ShardedJedisPool shardedJedisPool;
    
    @Autowired
    public RedisTemplate(ShardedJedisPool shardedJedisPool){
    	this.shardedJedisPool = shardedJedisPool;
    }


    /**
     * 设置一个key的过期时间（单位：秒）
     *
     * @param key     key值
     * @param seconds 多少秒后过期
     * @return 1：设置了过期时间  0：没有设置过期时间/不能设置过期时间
     */
    public long expire(String key, int seconds) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.expire(key, seconds);
        }
    }

    /**
     * 设置一个key在某个时间点过期
     *
     * @param key           key值
     * @param unixTimestamp unix时间戳，从1970-01-01 00:00:00开始到现在的秒数
     * @return 1：设置了过期时间  0：没有设置过期时间/不能设置过期时间
     */
    public long expireAt(String key, int unixTimestamp) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.expireAt(key, unixTimestamp);
        }
    }

    /**
     * 截断一个List
     *
     * @param key   列表key
     * @param start 开始位置 从0开始
     * @param end   结束位置
     * @return 状态码
     */
    public String trimList(String key, long start, long end) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.ltrim(key, start, end);
        }
    }

    /**
     * 检查Set长度
     */
    public long countSet(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.scard(key);
        }
    }

    /**
     * 添加到Set中（同时设置过期时间）
     *
     * @param key     key值
     * @param seconds 过期时间 单位s
     */
    public boolean addSet(String key, int seconds, String... value) {
        boolean result = addSet(key, value);
        if (result) {
            long i = expire(key, seconds);
            return i == 1;
        }
        return false;
    }

    /**
     * 添加到Set中
     */
    public boolean addSet(String key, String... value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.sadd(key, value);

            return true;
        }
    }


    /**
     * @return 判断值是否包含在set中
     */
    public boolean containsInSet(String key, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {

            return shardedJedis.sismember(key, value);
        }
    }

    /**
     * 获取Set
     */
    public Set<String> getSet(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {

            return shardedJedis.smembers(key);
        }
    }

    /**
     * 从set中删除value
     */
    public boolean removeSetValue(String key, String... value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.srem(key, value);
            return true;
        }
    }


    /**
     * 从list中删除value 默认count 1
     *
     * @param values 值list
     */
    public int removeListValue(String key, List<String> values) {
        return removeListValue(key, 1, values);
    }

    /**
     * 从list中删除value
     *
     * @param values 值list
     */
    public int removeListValue(String key, long count, List<String> values) {
        int result = 0;
        if (values != null && values.size() > 0) {
            for (String value : values) {
                if (removeListValue(key, count, value)) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * 从list中删除value
     *
     * @param count 要删除个数
     */
    public boolean removeListValue(String key, long count, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.lrem(key, count, value);
            return true;
        }
    }

    /**
     * 截取List
     *
     * @param start 起始位置
     * @param end   结束位置
     */
    public List<String> rangeList(String key, long start, long end) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {

            return shardedJedis.lrange(key, start, end);
        }
    }

    /**
     * 检查List长度
     */
    public long countList(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {

            return shardedJedis.llen(key);
        }
    }

    /**
     * 添加到List中（同时设置过期时间）
     *
     * @param key     key值
     * @param seconds 过期时间 单位s
     */
    public boolean addList(String key, int seconds, String... value) {
        boolean result = addList(key, value);
        if (result) {
            long i = expire(key, seconds);
            return i == 1;
        }
        return false;
    }

    /**
     * 添加到List
     */
    public boolean addList(String key, String... value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.lpush(key, value);
            return true;
        }
    }

    /**
     * 添加到List(只新增)
     */
    public boolean addList(String key, List<String> list) {
        if (key == null || list == null || list.size() == 0) {
            return false;
        }
        for (String value : list) {
            addList(key, value);
        }
        return true;
    }

    /**
     * 获取List
     */
    public List<String> getList(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.lrange(key, 0, -1);
        }
    }

    /**
     * 设置HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @param value  Json String or String value
     */
    public boolean setHSet(String domain, String key, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.hset(domain, key, value);
            return true;
        }
    }

    /**
     * 获得HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return Json String or String value
     */
    public String getHSet(String domain, String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hget(domain, key);
        }
    }

    /**
     * 删除HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return 删除的记录数
     */
    public long delHSet(String domain, String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hdel(domain, key);
        }
    }

    /**
     * 删除HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return 删除的记录数
     */
    public long delHSet(String domain, String... key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hdel(domain, key);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param domain 域名
     * @param key    键值
     */
    public boolean existsHSet(String domain, String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hexists(domain, key);
        }
    }

    /**
     * 全局扫描hset
     *
     * @param match field匹配模式
     */
    public List<Map.Entry<String, String>> scanHSet(String domain, String match) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            int cursor = 0;

            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            Jedis jedis = shardedJedis.getShard(domain);
            ScanResult<Map.Entry<String, String>> scanResult;
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
            do {
                scanResult = jedis.hscan(domain, String.valueOf(cursor), scanParams);
                list.addAll(scanResult.getResult());
                cursor = Integer.parseInt(scanResult.getStringCursor());
            } while (cursor > 0);
            return list;
        }
    }


    /**
     * 返回 domain 指定的哈希集中所有字段的value值
     */

    public List<String> hvals(String domain) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hvals(domain);
        }
    }

    /**
     * 返回 domain 指定的哈希集中所有字段的key值
     */

    public Set<String> hkeys(String domain) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hkeys(domain);
        }
    }

    /**
     * 返回 domain 指定的哈希key值总数
     */
    public long lenHset(String domain) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.hlen(domain);
        }
    }

    /**
     * 设置排序集合
     */
    public boolean setSortedSet(String key, long score, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.zadd(key, score, value);
            return true;
        }
    }

    /**
     * 获得排序集合
     */
    public Set<String> getSoredSet(String key, long startScore, long endScore, boolean orderByDesc) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            if (orderByDesc) {
                return shardedJedis.zrevrangeByScore(key, endScore, startScore);
            } else {
                return shardedJedis.zrangeByScore(key, startScore, endScore);
            }
        }
    }

    /**
     * 计算排序长度
     */
    public long countSoredSet(String key, long startScore, long endScore) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            Long count = shardedJedis.zcount(key, startScore, endScore);
            return count == null ? 0L : count;
        }
    }

    /**
     * 删除排序集合
     */
    public boolean delSortedSet(String key, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            long count = shardedJedis.zrem(key, value);
            return count > 0;
        }
    }

    /**
     * 获得排序集合
     */
    public Set<String> getSoredSetByRange(String key, long startRange, long endRange, boolean orderByDesc) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            if (orderByDesc) {
                return shardedJedis.zrevrange(key, startRange, endRange);
            } else {
                return shardedJedis.zrange(key, startRange, endRange);
            }
        }
    }

    /**
     * 获得排序打分
     */
    public Double getScore(String key, String member) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.zscore(key, member);
        }
    }

    public boolean set(String key, String value, int second) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.setex(key, second, value);
            return true;
        }
    }

    public boolean setIfAbsent(String key, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            long ret = shardedJedis.setnx(key, value);
            return ret > 0;
        }
    }

    public boolean set(String key, String value) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.set(key, value);
            return true;
        }
    }

    public String get(String key, String defaultValue) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.get(key) == null ? defaultValue : shardedJedis.get(key);
        }
    }

    public boolean del(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            shardedJedis.del(key);
            return true;
        }
    }

    public long incr(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.incr(key);
        }
    }

    public long decr(String key) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            return shardedJedis.decr(key);
        }
    }

    public List<Object> pAddSet(String key, int seconds, String... values) {
        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            ShardedJedisPipeline pipeline = shardedJedis.pipelined();
            for(String value : values) {
                pipeline.sadd(key, value);
            }
            pipeline.expire(key, seconds);
            List<Object> result = pipeline.syncAndReturnAll();
            result.remove(result.size() -1);
            return result;
        }
    }
}