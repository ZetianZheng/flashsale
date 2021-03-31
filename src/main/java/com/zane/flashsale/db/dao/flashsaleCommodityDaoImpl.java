package com.zane.flashsale.db.dao;

import com.zane.flashsale.db.mappers.flashsaleCommodityMapper;
import com.zane.flashsale.db.po.flashsaleCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class flashsaleCommodityDaoImpl implements flashsaleCommodityDao {

    @Resource
    private flashsaleCommodityMapper flashsaleCommodityMapper;

    @Override
    public flashsaleCommodity queryflashsaleCommodityById(long commodityId) {
        return flashsaleCommodityMapper.selectByPrimaryKey(commodityId);
    }
}
