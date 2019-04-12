package com.swan.gmall.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.annotation.LoginRequest;
import com.swan.gmall.bean.*;
import com.swan.gmall.bean.enums.PaymentWay;
import com.swan.gmall.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class OrderController {

    @Reference
    CartManagerService cartManagerService;

    @Reference
    UserInfoService userInfoService;

    @Reference
    UserAddressService userAddressService;

    @Reference
    OrderMangerService orderManageService;

    @Reference
    SkuManagerService skuManagerService;

    /*
        点击订单结算页的提交订单按钮，将购物车数据封装成订单数据
     */
    @LoginRequest(isNeedLogin = true)
    @RequestMapping("submitOrder")
    public String submitOrder(String deliveryAddressId,String tradeCode,HttpServletRequest request,Model model){
        String userId = request.getParameter("userId");

        // 检查页面和缓存中的tradeCode
        boolean b = orderManageService.checkTradeCode(tradeCode, userId);
        if (b) {

            // 从缓存中获取购物车数据
            List<CartInfo> cartInfoList = cartManagerService.getCartInfoByRedis(userId);

            /*
                封装订单详情
             */

            // 订单详情
            List<OrderInfo> orderInfoList = new ArrayList<>();

            UserAddress userAddress = userAddressService.getUserAddressById(deliveryAddressId);
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setConsignee(userAddress.getConsignee());
            orderInfo.setTotalAmount(getTotalPrice(cartInfoList));
            orderInfo.setOrderStatus("订单已提交");
            orderInfo.setUserId(userId);
            orderInfo.setPaymentWay(PaymentWay.ONLINE);

            // 订单结束时间
            Calendar calendar = Calendar.getInstance();
            // 当前日期加一天
            calendar.add(Calendar.DATE,1);
            orderInfo.setExpireTime(calendar.getTime());

            orderInfo.setDeliveryAddress(userAddress.getUserAddress());
            orderInfo.setOrderComment("swan商城");

            // 订单编号由字符串拼接
            String outTradeNum = "swan" + userId;
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String strDate = format.format(date);
            outTradeNum += strDate + System.currentTimeMillis();
            orderInfo.setOutTradeNo(outTradeNum);

            orderInfo.setCreateTime(new Date());
            orderInfo.setPaymentWay("订单已提交");
            orderInfo.setConsigneeTel(userAddress.getPhoneNum());


            /*
                封装订单商品详情对象
            */

            // 存储订单商品集合
            List<OrderDetail> orderDetailList = new ArrayList<>();
            // 用户购物车id
            List<String> cartIds = new ArrayList<>();

            for (CartInfo cartInfo : cartInfoList) {
                // 如果当前购物车商品被选中
                if ("1".equals(cartInfo.getIsChecked())) {
                    // 比较sku商品的价格和购物车中商品价格是否相等
                    SkuInfo skuInfo = skuManagerService.getSkuInfoById(cartInfo.getSkuId());
                    int i = skuInfo.getPrice().compareTo(cartInfo.getSkuPrice());
                    if (i == 0) {
                        // 商品订单集合
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setSkuId(cartInfo.getSkuId());
                        orderDetail.setSkuNum(cartInfo.getSkuNum());
                        orderDetail.setSkuName(cartInfo.getSkuName());
                        orderDetail.setImgUrl(cartInfo.getImgUrl());
                        orderDetail.setOrderPrice(cartInfo.getCartPrice());
                        orderDetailList.add(orderDetail);

                        cartIds.add(cartInfo.getId());
                    } else {
                        return "tradeFail";
                    }
                }
            }

            orderInfo.setOrderDetailList(orderDetailList);

            // 将购物车数据保存在订单中
            orderManageService.saveOrderInfo(orderInfo);

            // 购物车数据添加到订单数据中后，删除购物车数据
            //cartManagerService.delCartInfo(cartIds);

            return "pay";
        } else {
            return "tradeFail";
        }

    }

    @LoginRequest(isNeedLogin = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, Model model) {
        String userId = request.getParameter("userId");

        // 根据当前登录用户的userId查询存放在缓存中的购物车数据
        List<CartInfo> cartInfoByRedis = cartManagerService.getCartInfoByRedis(userId);

        List<OrderDetail> orderDetailList = new ArrayList<>();

        // 遍历获取每一个购物车商品信息
        for (CartInfo cartInfo : cartInfoByRedis) {
            if ("1".equals(cartInfo.getIsChecked())) {
                // 创建订单列表对象将购物车的数据封装成订单列表数据
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setHasStock("1");

                // 封装到订单列表集合中
                orderDetailList.add(orderDetail);
            }
        }

        // 获取当前用户的收货人信息
        List<UserAddress> userAddresseList = userInfoService.getLoginUserAddressByCache(userId);

        // 订单列表数据
        model.addAttribute("orderDetailList", orderDetailList);
        // 用户收货人信息
        model.addAttribute("userAddressList", userAddresseList);

        model.addAttribute("totalAmount", getTotalPrice(cartInfoByRedis));

        // 生成交易码tradeCode，写入缓存中
        String tradeCode = UUID.randomUUID().toString();
        model.addAttribute("tradeCode", tradeCode);

        orderManageService.generateTradeCode(tradeCode,userId);
        return "trade";
    }

    // 获取订单总价
    public BigDecimal getTotalPrice(List<CartInfo> cartInfoList) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (CartInfo info : cartInfoList) {
            if ("1".equals(info.getIsChecked())) {
                bigDecimal = bigDecimal.add(info.getCartPrice());
            }
        }

        return bigDecimal;
    }

}
