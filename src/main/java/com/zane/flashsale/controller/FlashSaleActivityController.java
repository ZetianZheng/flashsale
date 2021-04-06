package com.zane.flashsale.controller;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.FlashSaleCommodityDao;
import com.zane.flashsale.db.dao.OrderDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import com.zane.flashsale.db.po.FlashSaleCommodity;
import com.zane.flashsale.db.po.FlashSaleOrder;
import com.zane.flashsale.services.FLashSaleActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class FlashSaleActivityController {

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    @Autowired
    private FlashSaleCommodityDao flashSaleCommodityDao;

    @Autowired
    FLashSaleActivityService fLashSaleActivityService;

    @Autowired
    OrderDao orderDao;

    /**
     * add item page
     * after manager adding items, click commit, then it will redirect to addFlashSaleActivityAction
     * in add_activity.html: th:action="@{/addFlashSaleActivityAction}"
     * @return
     */
    @RequestMapping("/addFlashSaleActivity") // mapping address
    public String addFlashSaleActivity() {
        return "add_activity"; // this is not just return the string. It find template: add_activity, render it and return it to the server
    }

    /**
     * add success page
     * put result to resultmap and render it,
     * update database through Dao
     * @param name
     * @param commodityId
     * @param flashSalePrice
     * @param oldPrice
     * @param flashSaleNumber
     * @param startTime
     * @param endTime
     * @param resultmap
     * @return
     * @throws ParseException
     */
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

        flashSaleActivityDao.insertFlashSaleActivity(flashSaleActivity); // insert by Dao to database
        resultmap.put("flashSaleActivity", flashSaleActivity); // spring boot will automatically render page by finding these data.

        // return flashSaleActivity.toString(); // return this string and render it to browser, if has annotation: @ResponseBody
        return "add_success";
    }

    /**
     * item lists, get all active items and display them
     * @param resultMap
     * @return flashsale_activity
     */
    @RequestMapping("/flashsales")
    public String activityList(Map<String, Object> resultMap) {
        List<FlashSaleActivity> flashSaleActivities = flashSaleActivityDao.queryflashsaleActivitysByStatus(1); // 0 下架，1 正常
        resultMap.put("flashSaleActivities", flashSaleActivities);
        return "flashsale_activity";
    }


    /**
     * item's details page
     * render result map
     * @param resultMap
     * @param flashSaleActivityId
     * @return flashsale_item
     */
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
//        resultMap.put("commodityName", flashSaleCommodity.getCommodityName());
//        resultMap.put("commodityDesc", flashSaleCommodity.getCommodityDesc());

        return "flashsale_item";
    }

    /**
     * generate order,
     * First check if stock is available(using redis+LUA)
     * then call function: 'createOrder()' from fLashSaleActivityService
     * @param userId
     * @param flashsaleActivityId
     * @return flashsale_result
     */
    @RequestMapping("/flashsale/buy/{userId}/{flashsaleActivityId}")
    public ModelAndView flashSaleCommodity(
            @PathVariable long userId,
            @PathVariable long flashsaleActivityId
    ) {
        boolean stockValidateResult = false;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("flashsale_result");

        try {
            stockValidateResult = fLashSaleActivityService.flashSaleStockValidator(flashsaleActivityId);
            if (stockValidateResult) {
                FlashSaleOrder flashSaleOrder = fLashSaleActivityService.createOrder(flashsaleActivityId, userId);
                modelAndView.addObject("resultInfo", "success, order has been created, the orderId: " + flashSaleOrder.getOrderNo());
                modelAndView.addObject("orderNo", flashSaleOrder.getOrderNo());
                log.info("orderNo has been generated: " + flashSaleOrder.getOrderNo());
            } else {
                modelAndView.addObject("resultInfo", "failed, no available stock!");
                log.info("no available stock!");
            }
        } catch(Exception e) {
            log.error("system abnormal: " + e.toString());
            modelAndView.addObject("resultInfo", "flash sale failed!");
        }

        return modelAndView;
    }

    /**
     * get order information
     * @param orderNo
     * @return to view: order
     */
    @RequestMapping("/flashsale/orderQuery/{orderNo}")
    public ModelAndView orderQuery(@PathVariable String orderNo) {
        log.info("searching order, orderId: " + orderNo);
        FlashSaleOrder order = orderDao.queryOrder(orderNo);
        ModelAndView modelAndView = new ModelAndView();

        if (order != null) {
            modelAndView.setViewName("order"); // view name -> order.html
            modelAndView.addObject("order", order); // attribute order, point to order, ex: ${order.orderNo} in order.html
            FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(order.getFlashsaleActivityId());
            modelAndView.addObject("flashSaleActivity", flashSaleActivity);
        } else {
            modelAndView.setViewName("order_wait");
        }

        return modelAndView;
    }

    @RequestMapping("/flashsale/payOrder/{orderNo}")
    public String payOrder(@PathVariable String orderNo) throws Exception{
        log.info("paying order: " + orderNo);
        fLashSaleActivityService.payOrderProcess(orderNo);
        return "redirect:/flashsale/orderQuery/" + orderNo;
    }

}
