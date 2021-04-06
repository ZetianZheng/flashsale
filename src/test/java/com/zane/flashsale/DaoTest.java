package com.zane.flashsale;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.mappers.FlashSaleActivityMapper;
import com.zane.flashsale.db.po.FlashSaleActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@SpringBootTest
public class DaoTest {

    @Resource
    private FlashSaleActivityMapper flashsaleActivityMapper;

    @Autowired
    private FlashSaleActivityDao flashsaleActivityDao;

    @Test
    void flashsaleActivityTest() {
        FlashSaleActivity flashsaleActivity = new FlashSaleActivity();
        flashsaleActivity.setName("测试");
        flashsaleActivity.setCommodityId(999L);
        flashsaleActivity.setTotalStock(100L);
        flashsaleActivity.setFlashsalePrice(new BigDecimal(99));
        flashsaleActivity.setActivityStatus(16);
        flashsaleActivity.setOldPrice(new BigDecimal(99));
        flashsaleActivity.setAvailableStock(100);
        flashsaleActivity.setLockStock(0L);
        flashsaleActivityMapper.insert(flashsaleActivity);

        System.out.println("====>>>>" + flashsaleActivityMapper.selectByPrimaryKey(1L));
    }

    @Test
    void setflashsaleActivityQuery(){
        List<FlashSaleActivity> flashsaleActivitys =
                flashsaleActivityDao.queryflashsaleActivitysByStatus(0);

        System.out.println(flashsaleActivitys.size());

        flashsaleActivitys.stream().forEach(flashsaleActivity ->
                System.out.println(flashsaleActivity.toString()));
    }

    @Test
    public void test4j() {
        log.info("info");
    }

}