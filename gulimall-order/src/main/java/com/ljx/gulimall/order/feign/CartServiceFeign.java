package com.ljx.gulimall.order.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.order.model.vo.CartItemVo;
import com.ljx.gulimall.order.model.vo.MemberReceiveAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartServiceFeign {

    @GetMapping("/getCartItems")
    R<List<CartItemVo>> getCartItems();

}
