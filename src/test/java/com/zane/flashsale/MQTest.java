package com.zane.flashsale;

import com.zane.flashsale.mq.RocketMQService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MQTest {

    @Autowired
    RocketMQService rocketMQService;

    @Test
    public void SendTestMessage() throws Exception {
        rocketMQService.sendMessage("test", "Hello amigo!" + new Date().toString());
        System.out.println(new Date().toString());
    }
}
