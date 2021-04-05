package com.zane.flashsale.controller;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.FlashSaleCommodityDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import com.zane.flashsale.db.po.FlashSaleCommodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Controller
public class FlashSaleActivityController {

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    @Autowired
    private FlashSaleCommodityDao flashSaleCommodityDao;

    @RequestMapping("/addFlashSaleActivity") // mapping address
    public String addFlashSaleActivity() {
        return "add_activity"; // this is not just return the string. It find template: add_activity, render it and return it to the server
    }

    // response body 表明：这里的输出不再是返回值，而是直接注入html给前端。去掉response body则返回一个模板给前端
    @RequestMapping("/addFlashSaleActivityAction")
    public String addFlashSaleActivityAction(@RequestParam("name") String name,
                                             @RequestParam("commodityId") long commodityId,
                                             @RequestParam("flashSalePrice") BigDecimal flashSalePrice,
                                             @RequestParam("oldPrice") BigDecimal oldPrice,
                                             @RequestParam("flashSaleNumber") long flashSaleNumber,
                                             @RequestParam("startTime") String startTime,
                                             @RequestParam("endTime") String endTime,
                                             Map<String, Object> resultmap) throws ParseException {
        startTime = startTime.substring(0, 10) + startTime.substring(11);
        endTime = endTime.substring(0, 10) + endTime.substring(11);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");

        FlashSaleActivity flashSaleActivity = new FlashSaleActivity();
        flashSaleActivity.setName(name);
        flashSaleActivity.setCommodityId(commodityId);
        flashSaleActivity.setFlashsalePrice(flashSalePrice);
        flashSaleActivity.setOldPrice(oldPrice);
        flashSaleActivity.setTotalStock(flashSaleNumber);
        flashSaleActivity.setAvailableStock(new Integer(""+flashSaleNumber));
        flashSaleActivity.setLockStock(0L); // when rest stock is 0, suspend
        flashSaleActivity.setActivityStatus(1);
        flashSaleActivity.setStartTime(format.parse(startTime));
        flashSaleActivity.setEndTime(format.parse(endTime));

        flashSaleActivityDao.insertFlashSaleActivity(flashSaleActivity); // insert
        resultmap.put("flashSaleActivity", flashSaleActivity); // spring boot will automatically render page by finding these data.

        // return flashSaleActivity.toString(); // return this string and render it, if has annotation: @ResponseBody
        return "add_success";
    }

    @RequestMapping("/flashsales")
    public String activityList(Map<String, Object> resultMap) {
        List<FlashSaleActivity> flashSaleActivities = flashSaleActivityDao.queryflashsaleActivitysByStatus(1); // 0 下架，1 正常
        resultMap.put("flashSaleActivities", flashSaleActivities);
        return "flashsale_activity";
    }

    @RequestMapping("/item/{flashSaleActivityId}")
    public String itemPage(Map<String, Object> resultMap,
                           @PathVariable long flashSaleActivityId) {
        FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(flashSaleActivityId); // get activity by id
        FlashSaleCommodity flashSaleCommodity = flashSaleCommodityDao.queryflashsaleCommodityById(flashSaleActivity.getCommodityId()); // get commodity by its' id

        resultMap.put("flashSaleActivity", flashSaleActivity);
        resultMap.put("flashSaleCommodity", flashSaleCommodity);
        resultMap.put("flashSalePrice", flashSaleActivity.getFlashsalePrice());
        resultMap.put("oldPrice", flashSaleActivity.getOldPrice());

        resultMap.put("commodityId", flashSaleActivity.getCommodityId());
        resultMap.put("commodityName", flashSaleCommodity.getCommodityName());
        resultMap.put("commodityDesc", flashSaleCommodity.getCommodityDesc());

        return "flashsale_item";
    }
}
