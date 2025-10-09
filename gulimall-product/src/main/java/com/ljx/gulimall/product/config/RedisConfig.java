package com.ljx.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-08-05  18:00
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redisClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        return Redisson.create(config);
    }

}
