package com.zane.flashsale;

import com.zane.flashsale.services.ActivityHtmlPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ThymeleafServiceTest {

    @Autowired
    ActivityHtmlPageService activityHtmlPageService;

    @Test
    public void ThymeleafTest() {
        activityHtmlPageService.createActivityHtml(19);
    }
}
