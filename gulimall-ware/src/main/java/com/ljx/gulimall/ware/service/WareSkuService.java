package com.ljx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.ware.model.dto.OrderStockLockDTO;
import com.ljx.gulimall.ware.model.dto.StockLockDTO;
import com.ljx.gulimall.ware.model.entity.WareSkuEntity;
import com.ljx.gulimall.ware.model.vo.OrderStockLockVO;
import com.ljx.gulimall.ware.model.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:01:01
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuHasStockVo> getHashStock(List<Long> skuIds);

    Boolean lockOrderStock(OrderStockLockDTO orderStockLockDTO);

    Boolean unLockStock(StockLockDTO stockLockDTO);

    Boolean unLockStock(String orderSn);

}

