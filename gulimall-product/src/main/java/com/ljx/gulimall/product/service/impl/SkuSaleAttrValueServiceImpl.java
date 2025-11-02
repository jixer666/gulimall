package com.ljx.gulimall.product.service.impl;

import com.ljx.common.utils.AssertUtil;
import com.ljx.gulimall.product.model.vo.SpuSaleAttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.SkuSaleAttrValueDao;
import com.ljx.gulimall.product.model.entity.SkuSaleAttrValueEntity;
import com.ljx.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SpuSaleAttrVO> getSkuSaleAttrValueBySpuId(Long spuId) {
        AssertUtil.isNotEmpty(spuId, "spuId不能为空");

        return skuSaleAttrValueDao.selectSkuSaleAttrValueBySpuId(spuId);
    }

    @Override
    public List<String> getListBySkuId(Long skuId) {
        AssertUtil.isNotEmpty(skuId, "skuId不能为空");

        return skuSaleAttrValueDao.selectSkuSaleAttrValueBySkuId(skuId);
    }
}