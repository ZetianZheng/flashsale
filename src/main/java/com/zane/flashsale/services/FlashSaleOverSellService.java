package com.zane.flashsale.services;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlashSaleOverSellService {

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    /**
     * simply process flash sale
     * @param activityId
     * @return
     */
    public String processFlashSale(long activityId) {
        FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(activityId);

        long availableStock = flashSaleActivity.getAvailableStock();
        String result;

        if (availableStock > 0) {
            result = "Congratulations! your get it!";
            availableStock -= 1;
            flashSaleActivity.setAvailableStock(new Integer("" + availableStock));

            flashSaleActivityDao.updateflashsaleActivity(flashSaleActivity);
        } else {
            result = "all commodities has been sold out";
        }
        log.info(result);
        return result;
    }
}
