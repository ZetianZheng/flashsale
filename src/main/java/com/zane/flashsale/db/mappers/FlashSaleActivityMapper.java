package com.zane.flashsale.db.mappers;

import com.zane.flashsale.db.po.FlashSaleActivity;

import java.util.List;

public interface FlashSaleActivityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FlashSaleActivity record);

    int insertSelective(FlashSaleActivity record);

    FlashSaleActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashSaleActivity record);

    int updateByPrimaryKey(FlashSaleActivity record);

    List<FlashSaleActivity> queryflashsaleActivitysByStatus(int activityStatus);

    int lockStock(Long flashSaleActivityId);

    int deductStock(Long flashsaleActivityId);

    int revertStock(Long flashsaleActivityId);
}