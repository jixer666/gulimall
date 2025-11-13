package com.ljx.gulimall.order.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.order.model.vo.AddressFareVO;
import com.ljx.gulimall.order.model.vo.CartItemVo;
import com.ljx.gulimall.order.model.vo.OrderStockLockVO;
import com.ljx.gulimall.order.model.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareServiceFeign {

    @PostMapping("/ware/waresku/hashstock")
    R<List<SkuHasStockVo>> getHashStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    R<AddressFareVO> getAddIdFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku//lock")
    R<Boolean> lockOrderStock(@RequestBody List<OrderStockLockVO> orderStockLockVOS);
}
