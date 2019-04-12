package com.swan.gmall.order.service.impl;

import com.swan.gmall.bean.OrderDetail;
import com.swan.gmall.bean.OrderInfo;
import com.swan.gmall.order.mapper.OrderDetailMapper;
import com.swan.gmall.order.mapper.OrderInfoMapper;
import com.swan.gmall.service.OrderMangerService;
import com.swan.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

public class OrderManagerServiceImpl implements OrderMangerService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public void generateTradeCode(String tradeCode, String userId) {
        Jedis jedis = redisUtil.getJedis();
        // 设置tradeCode和生命周期
        jedis.setex("user:" + userId + ":tradeCode",60*30,tradeCode);
        jedis.close();
    }

    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {
        boolean bool = false;

        // 查询缓存中的tradeCode
        Jedis jedis = redisUtil.getJedis();
        String tradeFromCache = jedis.get("user:" + userId + ":tradeCode");
        if (tradeCode.equals(tradeFromCache)) {
            bool = true;
            jedis.del("user:" + userId + ":tradeCode");
        }
        return bool;
    }

    @Override
    public void saveOrderInfo(OrderInfo orderInfo) {
        orderInfoMapper.insert(orderInfo);
        List<OrderDetail> detailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : detailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }
    }
}
