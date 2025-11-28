package com.ljx.gulimall.seckill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.ljx.common.constant.CacheConstant;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.seckill.feign.CouponFeignService;
import com.ljx.gulimall.seckill.feign.ProductFeignService;
import com.ljx.gulimall.seckill.model.dto.SeckillSessionDTO;
import com.ljx.gulimall.seckill.model.dto.SeckillSessionSkuDTO;
import com.ljx.gulimall.seckill.model.entity.SeckillSkuRelationEntity;
import com.ljx.gulimall.seckill.model.entity.SkuInfoEntity;
import com.ljx.gulimall.seckill.model.vo.SeckillSkuVo;
import com.ljx.gulimall.seckill.service.SeckillService;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void preLoadSeckillData() {
        // 获取近三天的秒杀日期（某一时间段的）
        R<List<SeckillSessionDTO>> last3DayDataResult = couponFeignService.getLast3DayData();
        if (last3DayDataResult.getCode() != 0) {
            return;
        }

        List<SeckillSessionDTO> seckillSessionDTOList = last3DayDataResult.getDataList(SeckillSessionDTO.class);
        if (CollUtil.isEmpty(seckillSessionDTOList)) {
            return;
        }

        // 获取sku信息
        Map<Long, SkuInfoEntity> skuMap = getSkuMap(seckillSessionDTOList);

        // 缓存redis
        seckillSessionDTOList.forEach(seckillSessionDTO -> {
            String key = CacheConstant.SECKILL_SESSION_CACHE_KEY + seckillSessionDTO.getCreateTime().getTime() + "_" + seckillSessionDTO.getEndTime().getTime() + "_" + seckillSessionDTO.getName();
            for (SeckillSkuRelationEntity seckillSkuRelationEntity : seckillSessionDTO.getSeckillSkuRelationList()) {
                String itemKey = CacheConstant.SECKILL_SESSION_SKU_CACHE_KEY + seckillSkuRelationEntity.getPromotionId() + "_" + seckillSkuRelationEntity.getPromotionSessionId() + "_" + seckillSkuRelationEntity.getSkuId();

                // 避免重复上架
                Object o = stringRedisTemplate.opsForHash().get(key, itemKey);
                if (Objects.nonNull(o)) {
                    continue;
                }

                SeckillSessionSkuDTO seckillSessionSkuDTO = new SeckillSessionSkuDTO();
                seckillSessionSkuDTO.setSkuInfo(skuMap.getOrDefault(seckillSkuRelationEntity.getSkuId(), null));
                seckillSessionSkuDTO.setSeckillSessionSkuInfo(seckillSkuRelationEntity);
                seckillSessionSkuDTO.setToken(RandomUtil.randomString(10));

                // 设置库存
                String skuCountKey = CacheConstant.SECKILL_SESSION_SKU_COUNT_CACHE_KEY + seckillSessionSkuDTO.getToken();
                RSemaphore semaphore = redissonClient.getSemaphore(skuCountKey);
                semaphore.trySetPermits(seckillSkuRelationEntity.getSeckillCount());

                stringRedisTemplate.opsForHash().put(key, itemKey, JSONUtil.toJsonStr(seckillSessionSkuDTO));
            }
        });
    }

    private Map<Long, SkuInfoEntity> getSkuMap(List<SeckillSessionDTO> seckillSessionDTOList) {
        Set<Long> skuIds = new HashSet<>();
        seckillSessionDTOList.stream().forEach(item -> {
            for (SeckillSkuRelationEntity seckillSkuRelationEntity : item.getSeckillSkuRelationList()) {
                skuIds.add(seckillSkuRelationEntity.getSkuId());
            }
        });
        R<List<SkuInfoEntity>> infoBySkuIdsResult = productFeignService.getInfoBySkuIds(new ArrayList<>(skuIds));
        if (infoBySkuIdsResult.getCode() != 0) {
            return null;
        }
        Map<Long, SkuInfoEntity> skuMap = infoBySkuIdsResult.getDataList(SkuInfoEntity.class)
                .stream()
                .collect(Collectors.toMap(SkuInfoEntity::getSkuId, Function.identity()));
        return skuMap;
    }

    @Override
    public List<SeckillSessionSkuDTO> getCurrentSeckillSkus() {
        Long currentTime = System.currentTimeMillis();
        Set<String> keys = stringRedisTemplate.keys(CacheConstant.SECKILL_SESSION_CACHE_KEY + "*");
        if (CollUtil.isEmpty(keys)) {
            return new ArrayList<>();
        }

        // 查询所有满足seckill_session:前缀的key集合
        for (String key : keys) {
            String replaceStr = key.replace(CacheConstant.SECKILL_SESSION_CACHE_KEY, "");
            String[] splitArray = replaceStr.split("_");

            Long beginTime = Long.parseLong(splitArray[0]);
            Long endTime = Long.parseLong(splitArray[1]);
            if (currentTime < beginTime ||  currentTime >  endTime) {
                continue;
            }

            // 获取所有的商品信息
            return stringRedisTemplate.opsForHash().values(key)
                    .stream()
                    .map(item -> {
                        SeckillSessionSkuDTO seckillSessionSkuDTO = JSONUtil.toBean(item.toString(), SeckillSessionSkuDTO.class);
                        seckillSessionSkuDTO.setStartTime(beginTime);
                        seckillSessionSkuDTO.setEndTime(endTime);
                        return seckillSessionSkuDTO;
                    }).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public SeckillSkuVo getSkuSeckillInfo(Long skuId) {
        AssertUtil.isNotEmpty(skuId, "skuId不能为空");

        for (SeckillSessionSkuDTO currentSeckillSkus : getCurrentSeckillSkus()) {
            if (!currentSeckillSkus.getSkuInfo().getSkuId().equals(skuId)) {
                continue;
            }

            SeckillSkuVo seckillSkuVo = BeanUtil.copyProperties(currentSeckillSkus.getSeckillSessionSkuInfo(), SeckillSkuVo.class);
            seckillSkuVo.setStartTime(currentSeckillSkus.getStartTime());
            seckillSkuVo.setEndTime(currentSeckillSkus.getEndTime());

            return seckillSkuVo;
        }

        return null;
    }
}
