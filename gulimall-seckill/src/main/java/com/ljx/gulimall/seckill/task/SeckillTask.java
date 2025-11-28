package com.ljx.gulimall.seckill.task;

import com.ljx.common.constant.CacheConstant;
import com.ljx.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class SeckillTask {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 商品提前预热
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void preLoadSeckillData() {
        System.out.println("开始商品预热");
        RLock seckillLock = redissonClient.getLock(CacheConstant.SECKILL_PRE_LOAD_CACHE_KEY);
        try {
            seckillLock.lock();
            seckillService.preLoadSeckillData();
        } catch (Exception e) {
            log.error("执行商品预热出错：{}", e.getMessage(), e);
        } finally {
            seckillLock.unlock();
        }
        System.out.println("商品预热结束");
    }



}
