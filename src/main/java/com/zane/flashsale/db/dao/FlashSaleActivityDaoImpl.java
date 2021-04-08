package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.mappers.FlashSaleActivityMapper;
import com.zane.flashsale.db.po.FlashSaleActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Repository
public class FlashSaleActivityDaoImpl implements FlashSaleActivityDao {

    @Resource
    private FlashSaleActivityMapper flashsaleActivityMapper;

    @Override
    public List<FlashSaleActivity> queryflashsaleActivitysByStatus(int activityStatus) {
        return flashsaleActivityMapper.queryflashsaleActivitysByStatus(activityStatus);
    }

    @Override
    public void insertFlashSaleActivity(FlashSaleActivity flashsaleActivity) {
        flashsaleActivityMapper.insert(flashsaleActivity);
    }

    @Override
    public FlashSaleActivity queryflashsaleActivityById(long activityId) {
        return flashsaleActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateflashsaleActivity(FlashSaleActivity flashsaleActivity) {
        flashsaleActivityMapper.updateByPrimaryKey(flashsaleActivity);
    }

    @Override
    public boolean lockStock(Long flashSaleActivityId) {
        int result = flashsaleActivityMapper.lockStock(flashSaleActivityId);
        if (result < 1) {
            log.error("available stock not enough! lock stock failed!");
            return false;
        }

        return true;
    }

    @Override
    public boolean deductStock(Long flashsaleActivityId) {
        int result = flashsaleActivityMapper.deductStock(flashsaleActivityId);
        if (result < 1) {
            log.error("deduct stock failed!");
            return false;
        }

        return true;
    }

    @Override
    public void revertStock(Long flashsaleActivityId) {
        int result = flashsaleActivityMapper.revertStock(flashsaleActivityId);
        if (result < 1) {
            log.info("revertStock failed!");
        }
    }
}
