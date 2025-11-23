package com.ljx.gulimall.ware.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StockLockDTO {

    private Long orderStockLockId;

    private List<Long> wareOrderTaskDetailIdList = new ArrayList<>();

}
