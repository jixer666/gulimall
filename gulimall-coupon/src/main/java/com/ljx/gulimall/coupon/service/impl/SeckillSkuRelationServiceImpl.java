package com.ljx.gulimall.coupon.service.impl;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.ljx.gulimall.coupon.model.entity.SeckillSkuRelationEntity;
import com.ljx.gulimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Autowired
    private SeckillSkuRelationDao seckillSkuRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                new QueryWrapper<SeckillSkuRelationEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<SeckillSkuRelationEntity> listBySeckillSessionId(List<Long> seckillIds) {
        return CollUtil.isEmpty(seckillIds) ? new ArrayList<>() : seckillSkuRelationDao.selectList(new  QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", seckillIds));
    }
}