package com.ljx.gulimall.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.gulimall.coupon.model.dto.SeckillSessionDTO;
import com.ljx.gulimall.coupon.model.entity.SeckillSkuRelationEntity;
import com.ljx.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.coupon.dao.SeckillSessionDao;
import com.ljx.gulimall.coupon.model.entity.SeckillSessionEntity;
import com.ljx.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSessionDao seckillSessionDao;

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionDTO> getLast3DayData() {
        // 获取3天内秒杀活动
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);

        Date startDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(threeDaysLater.atZone(ZoneId.systemDefault()).toInstant());

        List<SeckillSessionEntity> list = seckillSessionDao.selectList(
                new LambdaQueryWrapper<SeckillSessionEntity>()
                        .ge(SeckillSessionEntity::getStartTime, startDate)
                        .le(SeckillSessionEntity::getEndTime, endDate)
        );

        // 获取秒杀活动中的商品
        List<Long> seckillSessionIds = list.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
        Map<Long, List<SeckillSkuRelationEntity>> seckillSessionMap = seckillSkuRelationService.listBySeckillSessionId(seckillSessionIds).stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));

        return list.stream().map(item -> {
            SeckillSessionDTO seckillSessionDTO = BeanUtil.copyProperties(item, SeckillSessionDTO.class);
            seckillSessionDTO.setSeckillSkuRelationList(seckillSessionMap.get(item.getId()));
            return seckillSessionDTO;
        }).collect(Collectors.toList());
    }
}