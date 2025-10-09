package com.ljx.gulimall.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-08-05  18:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallRedissionTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void test1() {
        System.out.println(redissonClient);
    }
}
