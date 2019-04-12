package com.swan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jayway.jsonpath.internal.filter.ValueNode;
import com.swan.gmall.bean.*;
import com.swan.gmall.manage.mapper.*;
import com.swan.gmall.service.BaseCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class BaseCatalogServiceImpl implements BaseCatalogService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseCatalog1Mapper baseCatalogMapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseCatalog1> getCatalog1Items() {
        return baseCatalogMapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2Items(String catalog1Id) {
        BaseCatalog2 catalog2 = new BaseCatalog2();
        catalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(catalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3Items(String catalog2Id) {
        BaseCatalog3 catalog3 = new BaseCatalog3();
        catalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(catalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoByCatalog3Id(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> attrInfos = baseAttrInfoMapper.select(baseAttrInfo);
        for (BaseAttrInfo attrInfo : attrInfos) {
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrInfo.getId());
            List<BaseAttrValue> attrValues = baseAttrValueMapper.select(baseAttrValue);
            attrInfo.setAttrValueList(attrValues);
        }
        return attrInfos;
    }

    @Override
    public void removeAttrByIdList(String id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setId(id);

        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(id);

        baseAttrValueMapper.delete(baseAttrValue);


        baseAttrInfoMapper.deleteByPrimaryKey(id);
    }

    public static void main(String[] args) {
    }

}





