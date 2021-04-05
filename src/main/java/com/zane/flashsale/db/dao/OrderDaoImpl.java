package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.mappers.FlashSaleOrderMapper;
import com.zane.flashsale.db.po.FlashSaleOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Resource
    private FlashSaleOrderMapper orderMapper;

    @Override
    public void insertOrder(FlashSaleOrder order) {
        orderMapper.insert(order);
    }

    @Override
    public FlashSaleOrder queryOrder(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public void updateOrder(FlashSaleOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }
}
