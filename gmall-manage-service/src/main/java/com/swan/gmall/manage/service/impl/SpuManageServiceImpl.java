package com.swan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.swan.gmall.bean.*;
import com.swan.gmall.manage.mapper.*;
import com.swan.gmall.service.SpuManageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuManageServiceImpl implements SpuManageService {

    @Autowired
    SpuManageMapper spuManageMapper;

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<SpuInfo> getSpuItems(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        return spuManageMapper.select(spuInfo);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //由于spuId是几张表中共有的字段，所以需要挨个保存

        // 先保存spu_info表
        spuInfoMapper.insertSelective(spuInfo);
        String spuId = spuInfo.getId();

        // 再保存spu_img表
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuId);
            spuImageMapper.insertSelective(spuImage);
        }

        // 保存spu_sale_attr销售属性表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insertSelective(spuSaleAttr);

            // 获取spu_sale_attr_value销售属性值表
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            // 保存spu_sale_attr_value销售属性值表
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuId);
                spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
            }
        }

    }

    @Override
    public List<SpuSaleAttr> getSaleAttr(String spuId) {
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.select(spuSaleAttr);
        for (SpuSaleAttr saleAttr : spuSaleAttrs) {
            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrId(saleAttr.getSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.select(spuSaleAttrValue);
            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrs;
    }

    @Override
    public List<SpuImage> getSpuImgList(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        List<SpuImage> spuImages = spuImageMapper.select(spuImage);
        return spuImages;
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrValueList(String spuId, String skuId) {
        return spuSaleAttrMapper.selectSaleAttrValueList(spuId,skuId);
    }

}
