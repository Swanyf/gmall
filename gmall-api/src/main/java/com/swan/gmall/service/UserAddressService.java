package com.swan.gmall.service;

import com.swan.gmall.bean.UserAddress;

import java.util.List;

public interface UserAddressService {

    List<UserAddress> getUserAddressByUserId(String userId);

    UserAddress getUserAddressById(String addressID);
}
