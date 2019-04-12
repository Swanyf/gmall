package com.swan.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.swan.gmall.bean.UserAddress;
import com.swan.gmall.service.UserAddressService;
import com.swan.gmall.user.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getUserAddressByUserId(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserAddress getUserAddressById(String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        return userAddressMapper.selectOne(userAddress);
    }

}
