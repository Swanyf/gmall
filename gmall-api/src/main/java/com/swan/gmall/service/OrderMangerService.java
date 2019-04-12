package com.swan.gmall.service;

import com.swan.gmall.bean.OrderInfo;

public interface OrderMangerService{
    void generateTradeCode(String tradeCode, String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    void saveOrderInfo(OrderInfo orderInfo);
}
