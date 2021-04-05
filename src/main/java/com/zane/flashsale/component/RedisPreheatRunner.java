package com.zane.flashsale.component;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import com.zane.flashsale.utl.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * runner will get execute just after application context i created and before spring boot application start up.
 * it accept arguments, which are passed at time of server start up.
 */
@Component
public class RedisPreheatRunner implements ApplicationRunner {
    @Autowired
    RedisService redisService;

    @Autowired
    FlashSaleActivityDao flashSaleActivityDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<FlashSaleActivity> flashSaleActivities = flashSaleActivityDao.queryflashsaleActivitysByStatus(1);

        for (FlashSaleActivity flashSaleActivity : flashSaleActivities) {
            redisService.setValue("stock:" + flashSaleActivity.getId(), (long)flashSaleActivity.getAvailableStock());
        }
    }

}
