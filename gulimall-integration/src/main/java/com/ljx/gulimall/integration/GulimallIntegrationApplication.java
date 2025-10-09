package com.ljx.gulimall.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-06-17  22:14
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallIntegrationApplication.class, args);
    }
}
