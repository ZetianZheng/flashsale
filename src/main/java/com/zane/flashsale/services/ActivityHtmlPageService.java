package com.zane.flashsale.services;

import com.zane.flashsale.db.dao.FlashSaleActivityDao;
import com.zane.flashsale.db.dao.FlashSaleCommodityDao;
import com.zane.flashsale.db.po.FlashSaleActivity;
import com.zane.flashsale.db.po.FlashSaleCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ActivityHtmlPageService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private FlashSaleActivityDao flashSaleActivityDao;

    @Autowired
    private FlashSaleCommodityDao flashSaleCommodityDao;

    public void createActivityHtml(long flashActivityId) {

        PrintWriter writer = null;

        try {
            FlashSaleActivity flashSaleActivity = flashSaleActivityDao.queryflashsaleActivityById(flashActivityId);
            FlashSaleCommodity flashSaleCommodity = flashSaleCommodityDao.queryflashsaleCommodityById(flashSaleActivity.getCommodityId());

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("flashSaleActivity", flashSaleActivity);
            resultMap.put("flashSaleCommodity", flashSaleCommodity);
            resultMap.put("flashSalePrice", flashSaleActivity.getFlashsalePrice());
            resultMap.put("flashSaleOldPrice", flashSaleActivity.getOldPrice());
            resultMap.put("commodityId", flashSaleActivity.getCommodityId());
            resultMap.put("commodityName", flashSaleCommodity.getCommodityName());
            resultMap.put("commodityDesc", flashSaleCommodity.getCommodityDesc());

            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(resultMap);

            // 创建输出流
            File file = new File("src/main/resources/templates/" + "flashsale_item_" + flashActivityId + ".html");
            writer = new PrintWriter(file);
            // 执行页面静态化方法
            templateEngine.process("flashsale_item", context, writer);
        } catch (Exception e) {
            log.error(e.toString());
            log.error("page static failed" + flashActivityId);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
