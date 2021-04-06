package com.zane.flashsale.controller;

import com.zane.flashsale.services.FLashSaleActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class FlashSaleOverSellController {

//    @Autowired
//    private FlashSaleOverSellService flashSaleOverSellService;

    @Autowired
    private FLashSaleActivityService fLashSaleActivityService;

    /**
     * handle flash sale request without LUA
     * @param flashSaleActivityId
     * @return
//     */
//    @ResponseBody
//    @RequestMapping("/flashsale/{flashSaleActivityId}")
//    public String FlashSale(@PathVariable long flashSaleActivityId){
//        return flashSaleOverSellService.processFlashSale(flashSaleActivityId);
//    }

    /**
     * handle flash sale request with LUA
     * @param flashSaleActivityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/flashsale/{flashSaleActivityId}")
    public String FlashSaleCommodity(@PathVariable long flashSaleActivityId){
        boolean stockValidatorResult = fLashSaleActivityService.flashSaleStockValidator(flashSaleActivityId);
        log.info("购买商品是否成功: " + stockValidatorResult);
        return stockValidatorResult ? "congratulations!" : "sold out";
    }
}
