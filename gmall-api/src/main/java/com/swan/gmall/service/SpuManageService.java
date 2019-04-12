package com.swan.gmall.service;

import com.swan.gmall.bean.SkuInfo;
import com.swan.gmall.bean.SpuImage;
import com.swan.gmall.bean.SpuInfo;
import com.swan.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface SpuManageService {
    List<SpuInfo> getSpuItems(String catalog3Id);

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> getSaleAttr(String spuId);

    List<SpuImage> getSpuImgList(String spuId);

    List<SpuSaleAttr> getSaleAttrValueList(String spuId, String skuId);
}
