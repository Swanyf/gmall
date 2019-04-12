package com.swan.gmall.service;

import com.swan.gmall.bean.BaseAttrInfo;
import com.swan.gmall.bean.BaseAttrValue;
import com.swan.gmall.bean.BaseSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrService {
    void saveAttrList(BaseAttrInfo baseAttrInfo);

    List<BaseSaleAttr> getSaleAttrName();

    List<BaseAttrValue> getValueNameList(String id);

    void removeAttrValueNameById(String id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrInfo> getAttrInfosByJoin(String join);
}
