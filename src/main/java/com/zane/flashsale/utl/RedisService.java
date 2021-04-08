package com.zane.flashsale.utl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Slf4j
@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    /**
     * set value
     *
     * note:
     * why close():
     * redis.pool.max-active = 200, redis will not automatically release resource after using it,
     * so we need to close it manually.
     * for ex:
     * for (int i = 0; i < 1000; i++) {
     *    Jedis jedisClient = jedisPool.getResource();
     *    jedisClient.set(key, value.toString());
     *    // no close, wrong
     *  }
     */

    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }

    public void setValue(String key, Long value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value.toString());
        jedisClient.close();
    }

    /**
     * get value
     */
    public String getValue(String key) {
        Jedis jedisClient = jedisPool.getResource();
        String value = jedisClient.get(key);
        jedisClient.close();
        return value;
    }


    /**
     * available stock validate and reduction in cache with LUA script
     *
     * LUA script:
     * if redis.call('exists', KEYS[1]) == 1 then
     *                 local stock = tonumber(redis.call('get', KEYS[1]));
     *                 if (stock <= 0) then
     *                         return -1;
     *                 end;
     *                 redis.call('decr', KEYS[1]);
     *                 return stock - 1;
     *             end;
     *             return -1;
     */

    public boolean stockDeductValidator(String key) {
        try (Jedis jedisClient = jedisPool.getResource()) {
            String script = "if redis.call('exists', KEYS[1]) == 1 then\n" +
                    "                local stock = tonumber(redis.call('get', KEYS[1]));\n" +
                    "                if (stock <= 0) then\n" +
                    "                        return -1;\n" +
                    "                end;\n" +
                    "                redis.call('decr', KEYS[1]);\n" +
                    "                return stock - 1;\n" +
                    "            end;\n" +
                    "            return -1;";

            Long stock = (Long) jedisClient.eval(script, Collections.singletonList(key), Collections.emptyList());

            if (stock < 0) {
                log.info("sold out!");
                return false;
            } else {
                log.info("congratulations! you get commodity!");
            }

            return true;
        } catch (Throwable throwable) {
            log.error("stock deduct failed!: " + throwable.toString());

            return false;
        }
    }

    /**
     * add limit member to sets stored at KEY
     * @param activityId
     * @param userId
     */
    public void addLimitMember(long activityId, long userId) {
        // 1. get resource from jedis pool
        Jedis jedisClient = jedisPool.getResource();

        // 2. sadd users to limit them to the specific flash sale activity(one user only can buy one item in one activity)
        // Q: what if we want to limit each person 5 items?
        jedisClient.sadd("flashSaleActivity_users:"+activityId, String.valueOf(userId));
    }

    /**
     * whether user is in the set
     * @param activityId
     * @param userId
     * @return
     */
    public boolean isInLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        boolean sismember = jedisClient.sismember("flashSaleActivity_users:"+activityId, String.valueOf(userId));
        log.info("userId: {} activityId: {} is member {}", userId, activityId, sismember);
        return sismember;
    }

    /**
     * remove member from redis set
     * @param flashsaleActivityId
     * @param userId
     */
    public void removeLimitMember(Long flashsaleActivityId, Long userId) {
        // 1. get resource from jedis pool
        Jedis jedisClient = jedisPool.getResource();

        // 2. remove users from the set of redis stored at key
        // Q: what if we want to limit each person 5 items?
        jedisClient.srem("flashSaleActivity_users:"+flashsaleActivityId, String.valueOf(userId));
    }

}
