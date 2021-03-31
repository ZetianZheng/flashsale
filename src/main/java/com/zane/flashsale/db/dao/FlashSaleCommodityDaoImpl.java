package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.mappers.FlashSaleCommodityMapper;
import com.zane.flashsale.db.po.FlashSaleCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class FlashSaleCommodityDaoImpl implements FlashSaleCommodityDao {

    @Resource
    private FlashSaleCommodityMapper flashsaleCommodityMapper;

    @Override
    public FlashSaleCommodity queryflashsaleCommodityById(long commodityId) {
        return flashsaleCommodityMapper.selectByPrimaryKey(commodityId);
    }
}
