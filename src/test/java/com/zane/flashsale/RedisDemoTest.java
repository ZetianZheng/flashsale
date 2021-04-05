package com.zane.flashsale;

import com.zane.flashsale.utl.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private RedisService redisService;

    @Test
    public void setStockTest() {
        redisService.setValue("stock:19", 10L);
    }


    public void getStockTest(String key) {
        String stock = redisService.getValue(key);
        System.out.println(stock);
    }

    @Test
    public void stockDeductValidatorTest() {
        boolean result = redisService.stockDeductValidator("stock:19");
        System.out.println("result: " + result);
        getStockTest("stock:19");
    }
}
