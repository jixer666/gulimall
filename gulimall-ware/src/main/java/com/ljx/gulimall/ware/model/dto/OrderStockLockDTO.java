package com.ljx.gulimall.ware.model.dto;

import com.ljx.gulimall.ware.model.vo.OrderStockLockVO;
import lombok.Data;

import java.util.List;

@Data
public class OrderStockLockDTO {

    private String orderSn;

    private List<OrderStockLockVO> orderStockLockVOS;

}
