package com.ljx.gulimall.product.service.impl;

import com.ljx.common.utils.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.BrandDao;
import com.ljx.gulimall.product.model.entity.BrandEntity;
import com.ljx.gulimall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void updateStatus(BrandEntity brand) {
        AssertUtil.isNotEmpty(brand, "参数不能为空");
        AssertUtil.isNotEmpty(brand.getBrandId(), "ID不能为空");
        AssertUtil.isNotEmpty(brand.getShowStatus(), "状态不能为空");
        BrandEntity brandEntity = brandDao.selectById(brand.getBrandId());
        BeanUtils.copyProperties(brand, brandEntity);
        brandDao.updateById(brandEntity);
    }
}