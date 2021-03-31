package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.mappers.flashsaleActivityMapper;
import com.zane.flashsale.db.po.flashsaleActivity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class flashsaleActivityDaoImpl implements flashsaleActivityDao {

    @Resource
    private flashsaleActivityMapper flashsaleActivityMapper;

    @Override
    public List<flashsaleActivity> queryflashsaleActivitysByStatus(int activityStatus) {
        return flashsaleActivityMapper.queryflashsaleActivitysByStatus(activityStatus);
    }

    @Override
    public void inertflashsaleActivity(flashsaleActivity flashsaleActivity) {
        flashsaleActivityMapper.insert(flashsaleActivity);
    }

    @Override
    public flashsaleActivity queryflashsaleActivityById(long activityId) {
        return flashsaleActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateflashsaleActivity(flashsaleActivity flashsaleActivity) {
        flashsaleActivityMapper.updateByPrimaryKey(flashsaleActivity);
    }
}
