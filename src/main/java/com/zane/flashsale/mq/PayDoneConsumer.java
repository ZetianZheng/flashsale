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

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic="pay_done", consumerGroup = "pay_done_group")
public class PayDoneConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    /**
     * received pay done message, do actual deduction in mysql.
     * @param messageExt
     */
    @Override
    public void onMessage(MessageExt messageExt) {
        // 1. analysis order request message
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("pay done! generating order request: " + message);
        FlashSaleOrder flashSaleOrder = JSON.parseObject(message, FlashSaleOrder.class);
        // 2. deduct stock
        flashSaleActivityDao.deductStock(flashSaleOrder.getFlashsaleActivityId());
    }
}
