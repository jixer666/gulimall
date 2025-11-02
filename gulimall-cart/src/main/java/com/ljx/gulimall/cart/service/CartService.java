package com.ljx.gulimall.cart.service;

import com.ljx.gulimall.cart.domain.vo.CartItemVo;
import com.ljx.gulimall.cart.domain.vo.CartVo;

public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num);

    CartItemVo getCartInfoBySkuId(Long skuId);

    CartVo getCartInfo();

    void checkItem(Long skuId, Integer checked);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
