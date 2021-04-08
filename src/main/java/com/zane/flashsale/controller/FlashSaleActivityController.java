package com.zane.flashsale.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.FlashSaleCommodityDao;
import com.zane.flashsale.db.dao.OrderDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import com.zane.flashsale.db.po.FlashSaleCommodity;
import com.zane.flashsale.db.po.FlashSaleOrder;
import com.zane.flashsale.services.FLashSaleActivityService;
import com.zane.flashsale.utl.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Autowired
    RedisService redisService;

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
     * use sentinel to protect this service
     * @param resultMap
     * @return flashsale_activity
     */
    @RequestMapping("/flashsales")
    public String activityList(Map<String, Object> resultMap) {
        try (Entry entry = SphU.entry("flashSalesResource")) {
            // resources under protection
            List<FlashSaleActivity> flashSaleActivities = flashSaleActivityDao.queryflashsaleActivitysByStatus(1); // 0 下架，1 正常
            resultMap.put("flashSaleActivities", flashSaleActivities);
            return "flashsale_activity";
        } catch (BlockException ex) {
            log.error(ex.toString());
            return "wait";
        }

    }


    /**
     * item's details page
     * render result map
     * 增加缓存预热： 去掉最前面两行直接从数据库中读写，使用redis进行缓存预热 详见代码解释
     * 增加sentinel流量控制，丢掉一些过多的请求。
     * @param resultMap
     * @param flashSaleActivityId
     * @return flashsale_item
     */
    @RequestMapping("/item/{flashSaleActivityId}")
    public String itemPage(Map<String, Object> resultMap,
                           @PathVariable long flashSaleActivityId) {
        try (Entry entry = SphU.entry("FlashSaleItemResource")) {
            //        FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(flashSaleActivityId); // get activity by id
            //        FlashSaleCommodity flashSaleCommodity = flashSaleCommodityDao.queryflashsaleCommodityById(flashSaleActivity.getCommodityId()); // get commodity by id

            // 被保护的代码
            FlashSaleActivity flashSaleActivity;
            FlashSaleCommodity flashSaleCommodity;

            /**
             * 缓存预热
             * 秒杀活动信息商品信息写入redis缓存中。访问的时候先去缓存查询是否有缓存，没有再去数据库查询。
             */
            // flash sale activity redis 缓存预热
            String flashSaleActivityInfo = redisService.getValue("flashSaleActivityId:" + flashSaleActivityId);
            if (StringUtils.isNotEmpty(flashSaleActivityInfo)) {
                log.info("redis缓存数据:" + flashSaleActivityInfo);
                flashSaleActivity = JSON.parseObject(flashSaleActivityInfo, FlashSaleActivity.class); // parse JSON string to java objects.
            } else {
                flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(flashSaleActivityId); // get activity by id
            }
            // flash sale commodity redis 缓存预热
            String flashSaleCommodityInfo = redisService.getValue("flashSaleCommodityId:" + flashSaleActivity.getCommodityId());
            if (StringUtils.isNotEmpty(flashSaleCommodityInfo)) {
                log.info("redis缓存数据:" + flashSaleCommodityInfo);
                flashSaleCommodity = JSON.parseObject(flashSaleCommodityInfo, FlashSaleCommodity.class); // parse JSON string to java objects.
            } else {
                flashSaleCommodity = flashSaleCommodityDao.queryflashsaleCommodityById(flashSaleActivity.getCommodityId()); // get commodity by id
            }

            /**
             *  result map 获取data 并渲染网页
             */
            resultMap.put("flashSaleActivity", flashSaleActivity);
            resultMap.put("flashSaleCommodity", flashSaleCommodity);
            resultMap.put("flashSalePrice", flashSaleActivity.getFlashsalePrice());
            resultMap.put("oldPrice", flashSaleActivity.getOldPrice());

            resultMap.put("commodityId", flashSaleActivity.getCommodityId());
            resultMap.put("commodityName", flashSaleCommodity.getCommodityName());
            resultMap.put("commodityDesc", flashSaleCommodity.getCommodityDesc());

            return "flashsale_item";
        } catch (BlockException ex) {
            // 降级到wait 页面。
            log.error(ex.toString());
            return "wait";
        }
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

            /**
             * whether or not that user in the limit member list
             */
            if (redisService.isInLimitMember(flashsaleActivityId, userId)) {
                modelAndView.addObject("resultInfo", "sorry, you are in the limit member list! every customer can only buy once!");
                return modelAndView;
            }
            /**
             * whether or not that user can flash sale(judge stock)
             */
            stockValidateResult = fLashSaleActivityService.flashSaleStockValidator(flashsaleActivityId);
            if (stockValidateResult) {
                FlashSaleOrder flashSaleOrder = fLashSaleActivityService.createOrder(flashsaleActivityId, userId);
                modelAndView.addObject("resultInfo", "success, order has been created, the orderId: " + flashSaleOrder.getOrderNo());
                modelAndView.addObject("orderNo", flashSaleOrder.getOrderNo());
                // add user in limit member list
                redisService.addLimitMember(flashsaleActivityId, userId);
            } else {
                modelAndView.addObject("resultInfo", "failed, no available stock!");
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
            modelAndView.setViewName("wait");
        }

        return modelAndView;
    }

    /**
     * pay order by payOrder process
     * @param orderNo
     * @return
     * @throws Exception
     */
    @RequestMapping("/flashsale/payOrder/{orderNo}")
    public String payOrder(@PathVariable String orderNo) throws Exception{
        log.info("paying order: " + orderNo);
        fLashSaleActivityService.payOrderProcess(orderNo);
        return "redirect:/flashsale/orderQuery/" + orderNo;
    }


    /**
     * return server time to frontend.
     * frontend will poll() update time and obtain rest time to open flash sale activity.
     * @return
     */
    @ResponseBody
    @RequestMapping("/flashsale/getSystemTime")
    public String getSystemTime() {
        // set date format
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // new date() get server time
        String date = df.format(new Date());
        return date;
    }

}
