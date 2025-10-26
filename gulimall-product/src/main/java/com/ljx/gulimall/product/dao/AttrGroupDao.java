package com.ljx.gulimall.product.dao;

import com.ljx.gulimall.product.model.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljx.gulimall.product.model.vo.SpuItemAttrGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:57:30
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVO> selectAttrGroupBySpuIdAndCatelogId(@Param("spuId") Long spuId, @Param("categoryId") Long catalogId);
}
