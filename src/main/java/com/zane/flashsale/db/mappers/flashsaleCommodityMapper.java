package com.zane.flashsale.db.mappers;

import com.zane.flashsale.db.po.flashsaleCommodity;

public interface flashsaleCommodityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(flashsaleCommodity record);

    int insertSelective(flashsaleCommodity record);

    flashsaleCommodity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(flashsaleCommodity record);

    int updateByPrimaryKey(flashsaleCommodity record);
}