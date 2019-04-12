package com.swan.gmall.service;

import com.swan.gmall.bean.BaseAttrInfo;
import com.swan.gmall.bean.BaseCatalog1;
import com.swan.gmall.bean.BaseCatalog2;
import com.swan.gmall.bean.BaseCatalog3;

import java.util.List;

public interface BaseCatalogService {

    List<BaseCatalog1> getCatalog1Items();

    List<BaseCatalog2> getCatalog2Items(String catalog1Id);

    List<BaseCatalog3> getCatalog3Items(String catalog2Id);

    List<BaseAttrInfo> getAttrInfoByCatalog3Id(String catalog3Id);

    void removeAttrByIdList(String id);
}
