package com.ljx.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-06-14  12:44
 */
@MapperScan("com.ljx.gulimall.product.dao")
@SpringBootApplication
@EnableFeignClients
public class GulimallPorductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallPorductApplication.class, args);
    }
}
