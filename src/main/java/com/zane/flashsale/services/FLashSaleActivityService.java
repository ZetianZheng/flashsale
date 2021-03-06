package com.zane.flashsale.services;

import com.alibaba.fastjson.JSON;
import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.FlashSaleCommodityDao;
import com.zane.flashsale.db.dao.OrderDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import com.zane.flashsale.db.po.FlashSaleCommodity;
import com.zane.flashsale.db.po.FlashSaleOrder;
import com.zane.flashsale.mq.RocketMQService;
import com.zane.flashsale.utl.RedisService;
import com.zane.flashsale.utl.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class FLashSaleActivityService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    @Autowired
    private FlashSaleCommodityDao flashSaleCommodityDao;

    @Autowired
    private RocketMQService rocketMQService;

    @Autowired
    OrderDao orderDao;

    /**
     * datacenterId; 数据中心
     * machineId; 机器标识
     * 在分布式环境中可以从机器配置上读取
     * 单机开发环境中先写死
     */

     private SnowFlake snowFlake = new SnowFlake(1, 1);

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

    /**
     * 创建订单 *
     * @param flashSaleActivityId
     * @param userId
     * @return
     * @throws Exception
     */
    public FlashSaleOrder createOrder(long flashSaleActivityId, long userId) throws Exception {
        // 1. create the order
        FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(flashSaleActivityId);
        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();

        // 2. generate order number by snow flake algorithm
        flashSaleOrder.setOrderNo(String.valueOf(snowFlake.nextId()));
        flashSaleOrder.setFlashsaleActivityId(flashSaleActivity.getId());
        flashSaleOrder.setUserId(userId);
        flashSaleOrder.setOrderAmount(flashSaleActivity.getFlashsalePrice().longValue());

        // 3. send created order message
        rocketMQService.sendMessage("flashsale_order", JSON.toJSONString(flashSaleOrder));

        /**
         * 4. send delay message to check order status after 1m
         * broker: messageDelayLevel: 1s, 5s, 10s, 30s, 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
         */
        rocketMQService.sendDelayMessage("pay_status_check", JSON.toJSONString(flashSaleOrder), 5);
        return flashSaleOrder;
    }

    /**
     * complete payment, this time we deduct stock in the sql,
     * before this step, we only lock stock and wait for payment.
     * @param orderNo
     */
    public void payOrderProcess(String orderNo) throws Exception{
        log.info("complete payment, orderNo: " + orderNo);
        FlashSaleOrder order = orderDao.queryOrder(orderNo); // get order by its' order
        /*
        whether or not this order exists
         */
        if (order == null) {
            log.error("this order not exists: " + orderNo);
            return;
        }
        /*
        payment complete
         */
        order.setPayTime(new Date());
        // 0: no available stock, invalid order, 1: order generated, waiting for payment, 2: accomplish payment
        order.setOrderStatus(2);
        orderDao.updateOrder(order);

        // send payment completed message:
        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));
    }

    /**
     *  put flash sale commodity and activity information into redis for cache warming
     *
     */
    public void pushFlashSaleInfoToRedis(long flashsaleActivityId) {
        FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(flashsaleActivityId);
        redisService.setValue("flashSaleActivityId:"+flashsaleActivityId, JSON.toJSONString(flashSaleActivity));

        FlashSaleCommodity flashSaleCommodity = flashSaleCommodityDao.queryflashsaleCommodityById(flashSaleActivity.getCommodityId());
        redisService.setValue("flashSaleCommodityId:"+flashSaleActivity.getCommodityId(), JSON.toJSONString(flashSaleCommodity));
    }

}
