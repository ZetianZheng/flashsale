package com.zane.flashsale.mq;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RocketMQMessageListener(topic = "test", consumerGroup = "consumer-group")
public class ConsumerListener implements RocketMQListener<MessageExt> {

    /**
     * consumer consume message by receiving messageExt
     * @param messageExt
     */
    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(), "UTF-8");
            System.out.println("receive Message:" + body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
