package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.po.flashsaleActivity;

import java.util.List;

public interface flashsaleActivityDao {

    public List<flashsaleActivity> queryflashsaleActivitysByStatus(int activityStatus);

    public void inertflashsaleActivity(flashsaleActivity flashsaleActivity);

    public flashsaleActivity queryflashsaleActivityById(long activityId);

    public void updateflashsaleActivity(flashsaleActivity flashsaleActivity);
}
