package com.swan.gmall.service;

import com.swan.gmall.bean.SkuInfo;

import java.util.List;

public interface SkuManagerService {
    List<SkuInfo> getSkuInfoList(String spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    SkuInfo getItemsFromDB(String skuId);

    SkuInfo getItems(String skuId);

    List<SkuInfo> getSkuInfoListBySpuId(String spuId);

    List<SkuInfo> getSkuInfoListByCatalog3Id(String catalog3Id);

    SkuInfo getSkuInfoById(String skuId);
}
