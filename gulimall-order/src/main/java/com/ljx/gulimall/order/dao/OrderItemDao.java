package com.ljx.gulimall.order.dao;

import com.ljx.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:04:49
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
