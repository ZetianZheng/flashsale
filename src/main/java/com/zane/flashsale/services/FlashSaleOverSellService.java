package com.zane.flashsale.services;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlashSaleOverSellService {

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    public String processFlashSale(long activityId) {
        FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(activityId);

        long availableStock = flashSaleActivity.getAvailableStock();
        String result;

        if (availableStock > 0) {
            result = "Congratulations! your get it!";
            System.out.println(result);

            availableStock -= 1;
            flashSaleActivity.setAvailableStock(new Integer("" + availableStock));

            flashSaleActivityDao.updateflashsaleActivity(flashSaleActivity);
        } else {
            result = "all commodities has been sold out";
            System.out.println(result);
        }

        return result;
    }
}
