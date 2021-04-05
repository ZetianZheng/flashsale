package com.zane.flashsale.services;

import com.zane.flashsale.utl.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FLashSaleActivityService {
    @Autowired
    private RedisService redisService;

    /**
     * 调用redis service 返回
     * 判断商品是否还有库存
     * @param activityId 商品ID
     * @return
     */
    public boolean flashSaleStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return redisService.stockDeductValidator(key);
    }
}
