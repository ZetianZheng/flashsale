package com.zane.flashsale;

import com.zane.flashsale.db.dao.flashsaleActivityDao;
import com.zane.flashsale.db.mappers.flashsaleActivityMapper;
import com.zane.flashsale.db.po.flashsaleActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DaoTest {

    @Resource
    private flashsaleActivityMapper flashsaleActivityMapper;

    @Autowired
    private flashsaleActivityDao flashsaleActivityDao;

    @Test
    void flashsaleActivityTest() {
        flashsaleActivity flashsaleActivity = new flashsaleActivity();
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
        List<flashsaleActivity> flashsaleActivitys =
                flashsaleActivityDao.queryflashsaleActivitysByStatus(0);

        System.out.println(flashsaleActivitys.size());

        flashsaleActivitys.stream().forEach(flashsaleActivity ->
                System.out.println(flashsaleActivity.toString()));
    }

}