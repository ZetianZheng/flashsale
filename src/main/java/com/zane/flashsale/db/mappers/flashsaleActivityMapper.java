package com.zane.flashsale.db.mappers;

import com.zane.flashsale.db.po.flashsaleActivity;

import java.util.List;

public interface flashsaleActivityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(flashsaleActivity record);

    int insertSelective(flashsaleActivity record);

    flashsaleActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(flashsaleActivity record);

    int updateByPrimaryKey(flashsaleActivity record);

    List<flashsaleActivity> queryflashsaleActivitysByStatus(int activityStatus);
}