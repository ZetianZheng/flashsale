package com.zane.flashsale.mq;

import com.alibaba.fastjson.JSON;
import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.OrderDao;
import com.zane.flashsale.db.po.FlashSaleOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "flashsale_order", consumerGroup = "flash_sale_order_group")
public class OrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    /**
     * consumer message from FlashSaleAvtivityService.java: createOrder();
     * get the message, deserialize,
     * lockStock, waiting for payment complete
     * @param messageExt
     */
    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        // 1. analysis and create order request
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("RocketMQ received order request: " + message);
        FlashSaleOrder order = JSON.parseObject(message, FlashSaleOrder.class); // deserialize json into FlashSaleOrder
        order.setCreateTime(new Date());

        // 2. deduct available stock and lock stock
        // return 1: success
        boolean lockStockResult = flashSaleActivityDao.lockStock(order.getFlashsaleActivityId());
        // 0: no available stock, order create failed, 1: order create success, waiting for payment
        if (lockStockResult) {
            order.setOrderStatus(1);
        } else {
            order.setOrderStatus(0);
        }

        // 3. insert order to sql
        orderDao.insertOrder(order);
        log.info("order has been inserted to sql");
    }


}
