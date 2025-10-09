package com.ljx.gulimall.product.service.impl;

import com.ljx.common.utils.AssertUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.ProductAttrValueDao;
import com.ljx.gulimall.product.model.entity.ProductAttrValueEntity;
import com.ljx.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> getBySpuId(Long spuId) {
        AssertUtil.isNotEmpty(spuId, "spu ID不能为空");

        return lambdaQuery().eq(ProductAttrValueEntity::getSpuId, spuId).list();
    }
}