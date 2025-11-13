package com.ljx.gulimall.ware.dao;

import com.ljx.gulimall.ware.model.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:01:01
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<Long> selectWareListBySkuId(Long skuId);

    int lockStock(@Param("skuId") Long skuId, @Param("count") Integer count, @Param("wareId") Long wareId);
}
