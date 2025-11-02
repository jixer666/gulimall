package com.ljx.gulimall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-06-14  12:44
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableFeignClients
public class GulimallCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCartApplication.class, args);
    }
}
