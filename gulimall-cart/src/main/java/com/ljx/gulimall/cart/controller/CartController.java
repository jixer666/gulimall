package com.ljx.gulimall.cart.controller;

import com.ljx.common.utils.R;
import com.ljx.gulimall.cart.domain.vo.CartItemVo;
import com.ljx.gulimall.cart.domain.vo.CartVo;
import com.ljx.gulimall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/cart.html")
    public String cartPage(Model model) {
        CartVo cartInfo = cartService.getCartInfo();
        model.addAttribute("cart", cartInfo);

        return "cartList";
    }

    @GetMapping("/addCartItem")
    public String successPage(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) {
        cartService.addToCart(skuId, num);

        // 重回定向到另一个网页，解决重复提交问题
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html?skuId=" + skuId;
    }


    @GetMapping("/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo cartItemVo = cartService.getCartInfoBySkuId(skuId);
        model.addAttribute("cartItem", cartItemVo);

        return "success";
    }


    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer checked) {
        cartService.checkItem(skuId, checked);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.countItem(skuId, num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @ResponseBody
    @GetMapping("/getCartItems")
    public R<List<CartItemVo>> getCartItems() {
        List<CartItemVo> cartItems = cartService.getCartItems();

        return R.ok().put("data",  cartItems);
    }


}
