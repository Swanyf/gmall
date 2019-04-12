package com.swan.gmall.manage.mapper;

import com.swan.gmall.bean.SkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuInfoMapper extends Mapper<SkuInfo> {

    List<SkuInfo> selectSkuInfoListBySpuId(String spuId);
}
