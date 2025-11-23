package com.ljx.gulimall.ware.fegin;

import com.ljx.common.utils.R;
import com.ljx.gulimall.ware.model.entity.MemberReceiveAddressEntity;
import com.ljx.gulimall.ware.model.entity.OrderEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order")
public interface OrderServiceFeign {

    @GetMapping("/order/order/getByOrderSn/{orderSn}")
    R<OrderEntity> getByOrderSn(@PathVariable("orderSn") String orderSn);

    @GetMapping("/test/t1")
    R<String> test();
}
