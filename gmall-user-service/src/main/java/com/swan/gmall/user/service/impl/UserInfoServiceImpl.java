package com.swan.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.swan.gmall.bean.UserAddress;
import com.swan.gmall.bean.UserInfo;
import com.swan.gmall.service.UserInfoService;
import com.swan.gmall.user.mapper.UserAddressMapper;
import com.swan.gmall.user.mapper.UserInfoMapper;
import com.swan.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    public UserInfo getLoginInfoFromDB(UserInfo userInfo) {
        UserInfo user = new UserInfo();
        user.setLoginName(userInfo.getLoginName());
        user.setPasswd(userInfo.getPasswd());
        UserInfo info = userInfoMapper.selectOne(user);

        if (info != null) {
            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(info.getId());
            List<UserAddress> userAddressList = userAddressMapper.select(userAddress);
            info.setUserAddressList(userAddressList);
        }


        return info;
    }

    @Override
    public UserInfo getLoginInfoByRedis(UserInfo userInfo) {
        UserInfo user = null;
        Jedis jedis = redisUtil.getJedis();
        String userInfoStr = jedis.get("user:" + userInfo.getLoginName() + ":info");
        // 将json数据转为java格式
        user = JSON.parseObject(userInfoStr, UserInfo.class);
        // redis中为空
        if (user == null) {
            // 查询数据库
            user = getLoginInfoFromDB(userInfo);
            // 数据库中为空
            if (user == null) {
                return user;
            } else {
                // 同步redis数据
                jedis.setex("user:" + userInfo.getLoginName() + ":info", 60 * 60 * 24, JSON.toJSONString(user));
            }
        }
        jedis.close();

        return user;
    }

    @Override
    public List<UserAddress> getLoginUserAddressByCache(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }
}
