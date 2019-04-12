package com.swan.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.swan.gmall.annotation.LoginRequest;
import com.swan.gmall.bean.CartInfo;
import com.swan.gmall.bean.SkuInfo;
import com.swan.gmall.service.CartManagerService;
import com.swan.gmall.service.SkuManagerService;
import com.swan.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    SkuManagerService skuManagerService;

    @Reference
    CartManagerService cartManagerService;

    @LoginRequest(isNeedLogin = false)
    @RequestMapping("checkCart")
    public String checkCart(CartInfo cartInfo,HttpServletRequest request,HttpServletResponse response,Model model){

        String userId = (String) request.getAttribute("userId");

        List<CartInfo> cartInfos = null;

        // 用户登录直接从redis中拿取数据
        if (StringUtils.isNotBlank(userId)) {
            cartInfos = cartManagerService.getCartInfoByRedis(userId);
        } else {
            // 未登录从redis缓存中取数据
            String cartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            cartInfos = JSON.parseArray(cartCookie, CartInfo.class);
        }

        for (CartInfo info : cartInfos) {
            // 如果cookie中的skuId和请求的skuId相等，就设置成选中状态
            if (cartInfo.getSkuId().equals(info.getSkuId())) {
                info.setIsChecked(cartInfo.getIsChecked());
                if (StringUtils.isNotBlank(userId)) {
                    // 修改购物车商品的信息
                    cartManagerService.modifyCartInfo(info);
                    // 更新redis缓存
                    cartManagerService.synchronizedRedisCartInfo(userId);
                } else {
                    // 覆盖cookie
                    CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(cartInfos), 60 * 60 * 24, true);
                }
            }
        }

        BigDecimal totalPrice = getTotalPrice(cartInfos);

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartList",cartInfos);

        return "innerCartList";
    }

    @LoginRequest(isNeedLogin = false)
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request,HttpServletResponse response,Model model){

        String userId = (String) request.getAttribute("userId");

        List<CartInfo> cartInfos = null;

        if (StringUtils.isNotBlank(userId)) {
            // 根据userId查询存放在redis缓存中的购物车信息
            cartInfos = cartManagerService.getCartInfoByRedis(userId);
        } else {
            // 获取存放在cookie中的购物车信息
            String cartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            // 将浏览器cookie的JSON数据转成java格式
            cartInfos = JSON.parseArray(cartCookie, CartInfo.class);
        }

        // 获取当前被选中的商品，计算总价
        BigDecimal totalPrice = getTotalPrice(cartInfos);

        model.addAttribute("cartList",cartInfos);
        model.addAttribute("totalPrice", totalPrice);

        return "cartList";
    }

    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
        // 用于计算所有商品总价
        BigDecimal bigDecimal = new BigDecimal("0");

        for (CartInfo cartInfo : cartInfos) {
            // 获取商品选中状态
            String checked = cartInfo.getIsChecked();
            // 获取当前被选中的
            if ("1".equals(checked)) {
                bigDecimal = bigDecimal.add(cartInfo.getCartPrice());
            }
        }
        return bigDecimal;
    }

    @LoginRequest(isNeedLogin = false)
    @RequestMapping("addToCart")
    public String addToCart(String skuId, int num, HttpServletRequest request, HttpServletResponse response, Model model){

        String userId = (String) request.getAttribute("userId");

        // 创建购物车的对象
        CartInfo cartInfo = new CartInfo();

        // 根据skuId查询skuInfo的信息
        SkuInfo skuInfo = skuManagerService.getSkuInfoById(skuId);

        model.addAttribute("skuInfo",skuInfo);
        // 将查询的数据封装到购物车对象中
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setSkuNum(num);
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(num)));
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setIsChecked("1");

        List<CartInfo> cartList = new ArrayList<>();

        // 判断用户是否登录
        if (StringUtils.isBlank(userId)) {
            /*
                未登录就操作cookie对象
              */
            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            // 将浏览器的json数据转换成java集合对象
            cartList = JSON.parseArray(listCartCookie, CartInfo.class);

            // 校验
            // 如果获取到的cookie对象为空，说明是第一次添加到购物车
            if (StringUtils.isBlank(listCartCookie)) {
                // 初始化购物车集合对象
                cartList = new ArrayList<>();
                // 直接保存到购物车中
                cartList.add(cartInfo);
            } else {    // 获取到的cookie信息不为空，购物车不为空
                // 先判断要保存的商品与购物车中的商品是否重复
                Boolean bool = if_new_cart(cartList, cartInfo);
                if (bool) {
                    // 要保存的新的购物车对象
                    cartList.add(cartInfo);
                } else {
                    // 重复的购物车信息;-p0kn6
                    // 增加商品数量并计算该商品总价
                    for (CartInfo info : cartList) {
                        if (skuId.equals(info.getSkuId())) {
                            info.setSkuNum(info.getSkuNum() + cartInfo.getSkuNum());
                            info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                        }
                    }
                }
            }

            // 重新设置浏览器的cookie信息
            CookieUtil.setCookie(request, response, "listCartCookie", JSON.toJSONString(cartList), 60 * 60 * 24, true);
        } else {
            /*
                用户已经登录，操作数据库DB
              */

            // 判断本次操作是将商品添加到购物车还是修改购物车商品的数量和价格
            // 根据当前的userId和skuId查询购物车中是否存在该商品
            CartInfo info = cartManagerService.getCarInfoByUserIdAndSkuId(userId,skuId);

            // 不存在就做添加商品到数据库
            if (info == null) {
                cartManagerService.saveCartInfo(cartInfo);
            } else {    // 存在
                info.setSkuNum(info.getSkuNum() + num);
                info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                cartManagerService.modifyCartInfo(info);
            }

            // 将db的数据同步到redis缓存中
            cartManagerService.synchronizedRedisCartInfo(userId);

        }

        return "redirect:http://cart.gmall.com:8084/success.html";
    }

    private Boolean if_new_cart(List<CartInfo> cartInfos, CartInfo cartInfo) {

        boolean bool = true;
        for (CartInfo info : cartInfos) {
            if (info.getSkuId() == cartInfo.getSkuId()) {
                bool = false;
            }
        }

        return bool;

    }

}
