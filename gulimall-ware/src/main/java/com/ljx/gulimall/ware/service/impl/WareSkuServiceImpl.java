package com.ljx.gulimall.ware.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.gulimall.ware.model.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.ware.dao.WareSkuDao;
import com.ljx.gulimall.ware.model.entity.WareSkuEntity;
import com.ljx.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<SkuHasStockVo> getHashStock(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new ArrayList<>();
        }
        return wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                        .in("sku_id", skuIds))
                .stream().map(item -> {
                    SkuHasStockVo resp = new SkuHasStockVo();
                    resp.setSkuId(item.getSkuId());
                    resp.setHashStock(item.getStock() - item.getStockLocked() > 0);
                    return resp;
                }).collect(Collectors.toList());
    }
}