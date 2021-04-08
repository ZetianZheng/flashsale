package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.po.FlashSaleActivity;

import java.util.List;

public interface FlashSaleActivityDao {

    public List<FlashSaleActivity> queryflashsaleActivitysByStatus(int activityStatus);

    public void insertFlashSaleActivity(FlashSaleActivity flashsaleActivity);

    public FlashSaleActivity queryflashsaleActivityById(long activityId);

    public void updateflashsaleActivity(FlashSaleActivity flashsaleActivity);

    boolean lockStock(Long flashSaleActivityId);

    boolean deductStock(Long flashsaleActivityId);

    void revertStock(Long flashsaleActivityId);
}
