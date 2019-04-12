package com.swan.gmall.bean;

import java.io.Serializable;
import java.util.List;

public class UserAddressVO implements Serializable {

    private String userId;
    private List<String> userAddress;

    public UserAddressVO() {
    }

    public UserAddressVO(String userId, List<String> userAddress) {
        this.userId = userId;
        this.userAddress = userAddress;
    }

    @Override
    public String toString() {
        return "UserAddressVO{" +
                "userId='" + userId + '\'' +
                ", userAddress=" + userAddress +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(List<String> userAddress) {
        this.userAddress = userAddress;
    }
}
