package com.swan.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.swan.gmall.bean.CartInfo;
import com.swan.gmall.cart.mapper.CartInfoMapper;
import com.swan.gmall.service.CartManagerService;
import com.swan.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartManagerServiceImpl implements CartManagerService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public CartInfo getCarInfoByUserIdAndSkuId(String userId, String skuId) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        CartInfo info = cartInfoMapper.selectOne(cartInfo);
        return info;
    }

    @Override
    public void saveCartInfo(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    @Override
    public void modifyCartInfo(CartInfo info) {
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId", info.getUserId()).andEqualTo("skuId", info.getSkuId());
        cartInfoMapper.updateByExampleSelective(info, example);

    }

    // 同步db和redis中的数据
    @Override
    public void synchronizedRedisCartInfo(String userId) {
        // 先根据userId查询数据库的数据
        List<CartInfo> cartInfos = getCartInfoById(userId);

        // 获取redis连接
        Jedis jedis = redisUtil.getJedis();
        // 如果查询的数据不为空，就保存到redis中
        if (cartInfos != null) {
            // map 的键用于存放购物车id和购物车商品信息
            HashMap<String, String> map = new HashMap<>();
            // 将每一个购物车商品的信息存放到map中
            for (CartInfo cartInfo : cartInfos) {
                map.put(cartInfo.getId(), JSON.toJSONString(cartInfo));
            }
            // 存放redis的hash数据
            jedis.hmset("cart:" + userId + ":info", map);
        }
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartInfoByRedis(String userId) {
        Jedis jedis = redisUtil.getJedis();
        ArrayList<CartInfo> cartInfos = new ArrayList<>();

        List<String> strings = jedis.hvals("cart:" + userId + ":info");

        // 遍历每一个存储在redis中的json字符串
        for (String string : strings) {
            if (StringUtils.isNotBlank(string)) {
                // 将每一个字符串转换成javabean的cartInfo对象
                CartInfo cartInfo = JSON.parseObject(string, CartInfo.class);
                // 将对象存放到cartInfo的集合中
                cartInfos.add(cartInfo);
            }
        }
        return cartInfos;
    }

    @Override
    public void synchronizedDBCartInfo(String userId, String cartListCookie) {

        // 根据id查询购物车数据
        List<CartInfo> cartInfosFromDB = getCartInfoById(userId);

        // 获取cookie中的数据
        List<CartInfo> cartInfosFromCookie = JSON.parseArray(cartListCookie, CartInfo.class);

        // 判断cookie中的数据,数据库是否存在
        for (CartInfo info : cartInfosFromCookie) {
            Boolean bool = if_new_cart(cartInfosFromDB, info);
            if (bool) {
                // 不存在，直接将数据保存到DB中
                info.setUserId(userId);
                cartInfoMapper.insertSelective(info);
            } else {
                // 存在就更新DB数据
                for (CartInfo cartInfo : cartInfosFromDB) {
                    // 如果skuId商品id相同就修改
                    if (cartInfo.getSkuId().equals(info.getSkuId())) {
                        cartInfo.setSkuNum(cartInfo.getSkuNum() + info.getSkuNum());
                        cartInfo.setCartPrice(cartInfo.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
                        cartInfoMapper.updateByPrimaryKeySelective(cartInfo);
                    }
                }

            }
        }

        // 同步db和缓存中数据
        synchronizedRedisCartInfo(userId);

    }

    @Override
    public void delCartInfo(List<String> cartIds) {
        for (String cartId : cartIds) {
            cartInfoMapper.deleteByPrimaryKey(cartId);
        }
    }

    private List<CartInfo> getCartInfoById(String userId) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartInfoMapper.select(cartInfo);
        return cartInfos;
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
