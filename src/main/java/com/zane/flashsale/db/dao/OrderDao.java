package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.po.FlashSaleOrder;

public interface OrderDao {

    void insertOrder(FlashSaleOrder order);

    FlashSaleOrder queryOrder(String orderNo);

    void updateOrder(FlashSaleOrder order);
}
