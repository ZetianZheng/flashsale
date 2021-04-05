package com.zane.flashsale.db.mappers;

import com.zane.flashsale.db.po.FlashSaleOrder;

public interface FlashSaleOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FlashSaleOrder record);

    int insertSelective(FlashSaleOrder record);

    FlashSaleOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashSaleOrder record);

    int updateByPrimaryKey(FlashSaleOrder record);

    FlashSaleOrder selectByOrderNo(String orderNo);
}