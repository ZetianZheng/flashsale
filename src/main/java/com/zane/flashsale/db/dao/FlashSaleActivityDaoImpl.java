package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.mappers.FlashSaleActivityMapper;
import com.zane.flashsale.db.po.FlashSaleActivity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class FlashSaleActivityDaoImpl implements FlashSaleActivityDao {

    @Resource
    private FlashSaleActivityMapper flashsaleActivityMapper;

    @Override
    public List<FlashSaleActivity> queryflashsaleActivitysByStatus(int activityStatus) {
        return flashsaleActivityMapper.queryflashsaleActivitysByStatus(activityStatus);
    }

    @Override
    public void inertflashsaleActivity(FlashSaleActivity flashsaleActivity) {
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
}
