package com.zane.flashsale.utl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

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
                System.out.println("sold out!");
                return false;
            } else {
                System.out.println("congratulations! you get commodity!");
            }

            return true;
        } catch (Throwable throwable) {
            System.out.println("stock deduct failed!: " + throwable.toString());

            return false;
        }
    }
}
