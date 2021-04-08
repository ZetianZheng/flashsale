package com.zane.flashsale.mq;

import com.alibaba.fastjson.JSON;
import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.OrderDao;
import com.zane.flashsale.db.po.FlashSaleOrder;
import com.zane.flashsale.utl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic="pay_status_check", consumerGroup = "pay_check_group")
public class PayStatusCheckListener implements RocketMQListener<MessageExt> {
    // 不加component就不会在bean中注册这个组件，也就不会监听到
    // hint 2: 这里的@RocketMQMessageListener使用的是默认的集群消费，所有的消费者组中，只需要有一个消费者对这个信息进行消费就可以了
    @Autowired
    OrderDao orderDao;

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    @Resource
    private RedisService redisService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("received order check status message!" + message);
        // get order
        FlashSaleOrder order = JSON.parseObject(message, FlashSaleOrder.class);

        // check order status
        FlashSaleOrder orderInfo = orderDao.queryOrder(order.getOrderNo());

        if(orderInfo == null) {
            log.info("order is null, orderNo: " + order.getOrderNo());
            return;
        }

        if (orderInfo.getOrderStatus() != 2) {
            // if this order not been completed, cancel this order. 99
            log.info("order not been paid, it has been closed: " + orderInfo.getOrderNo());
            orderInfo.setOrderStatus(99);
            orderDao.updateOrder(orderInfo);

            // revert stock (stock + 1）
            flashSaleActivityDao.revertStock(order.getFlashsaleActivityId());

            // put this user out from "list: user who bought this item"
            redisService.removeLimitMember(order.getFlashsaleActivityId(), order.getUserId());
        }
    }
}
