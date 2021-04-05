package com.zane.flashsale.db.mappers;

import com.zane.flashsale.db.po.FlashSaleUser;

public interface FlashSaleUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FlashSaleUser record);

    int insertSelective(FlashSaleUser record);

    FlashSaleUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashSaleUser record);

    int updateByPrimaryKey(FlashSaleUser record);
}