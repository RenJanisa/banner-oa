package com.banneroa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.TimeZone;

@SpringBootApplication
@MapperScan("com.banneroa.mapper")
public class BannerOaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BannerOaApplication.class, args);
    }

    @PostConstruct
    void started() {
        //       TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        //      TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

}
