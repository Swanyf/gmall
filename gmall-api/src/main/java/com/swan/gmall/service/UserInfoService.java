package com.swan.gmall.service;

import com.swan.gmall.bean.UserAddress;
import com.swan.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    UserInfo getLoginInfoByRedis(UserInfo userInfo);

    List<UserAddress> getLoginUserAddressByCache(String userId);
}
