package com.ljx.gulimall.order.model.dto;

import com.ljx.gulimall.order.model.vo.OrderStockLockVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStockLockDTO {

    private String orderSn;

    private List<OrderStockLockVO> orderStockLockVOS;

}
