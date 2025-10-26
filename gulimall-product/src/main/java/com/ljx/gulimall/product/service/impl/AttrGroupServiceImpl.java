package com.ljx.gulimall.product.service.impl;

import com.ljx.common.utils.AssertUtil;
import com.ljx.gulimall.product.model.vo.SpuItemAttrGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.AttrGroupDao;
import com.ljx.gulimall.product.model.entity.AttrGroupEntity;
import com.ljx.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SpuItemAttrGroupVO> getAttrGroupBySpuIdAndCategoryId(Long spuId, Long catalogId) {
        AssertUtil.isNotEmpty(spuId, "spu ID不能为空");
        AssertUtil.isNotEmpty(catalogId, "分类ID不能为空");

        return attrGroupDao.selectAttrGroupBySpuIdAndCatelogId(spuId, catalogId);
    }

}