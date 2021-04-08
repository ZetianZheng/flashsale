package com.zane.flashsale;

import com.zane.flashsale.services.FLashSaleActivityService;
import com.zane.flashsale.utl.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private RedisService redisService;

    @Autowired
    private FLashSaleActivityService fLashSaleActivityService;

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

    @Test
    public void pushFlashSaleInfoToRedisTest() {
        fLashSaleActivityService.pushFlashSaleInfoToRedis(19);
    }

    @Test
    public void getFlashSaleInfoFromRedisTest() {
        String fsainfo = redisService.getValue("flashSaleActivityId:"+19);
        String fscinfo = redisService.getValue("flashSaleCommodityId:"+1001);
        System.out.println("fsa"+fsainfo);
        System.out.println("fsc"+fscinfo);
    }
}
