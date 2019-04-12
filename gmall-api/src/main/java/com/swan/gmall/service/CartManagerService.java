package com.swan.gmall.service;

import com.swan.gmall.bean.CartInfo;

import java.util.List;

public interface CartManagerService {

    CartInfo getCarInfoByUserIdAndSkuId(String userId, String skuId);

    void saveCartInfo(CartInfo cartInfo);

    void modifyCartInfo(CartInfo info);

    void synchronizedRedisCartInfo(String userId);

    List<CartInfo> getCartInfoByRedis(String userId);

    void synchronizedDBCartInfo(String userId, String cartListCookie);

    void delCartInfo(List<String> cartIds);
}
