package com.swan.gmall.service;

import com.swan.gmall.bean.SkuLsInfo;
import com.swan.gmall.bean.SkuLsParam;

import java.util.List;

public interface ListService {
    List<SkuLsInfo> getSkuLsInfos(SkuLsParam skuLsParam);
}
