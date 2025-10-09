package com.ljx.gulimall.product.service.impl;

import com.ljx.common.utils.AssertUtil;
import com.ljx.gulimall.product.model.enums.AttrSearchTpeEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.AttrDao;
import com.ljx.gulimall.product.model.entity.AttrEntity;
import com.ljx.gulimall.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> selectSearchAttrIds(List<Long> attrtIds) {
        AssertUtil.isFalse(attrtIds.isEmpty(), "商品属性列表为空");

        return lambdaQuery().eq(AttrEntity::getAttrType, AttrSearchTpeEnum.YES.getValue()).in(AttrEntity::getAttrId, attrtIds).list();
    }
}