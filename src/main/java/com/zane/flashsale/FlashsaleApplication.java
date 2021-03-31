package com.zane.flashsale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.zane.flashsale.db.mappers")
@ComponentScan(basePackages = {"com.zane"})
public class FlashsaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashsaleApplication.class, args);
    }

}
