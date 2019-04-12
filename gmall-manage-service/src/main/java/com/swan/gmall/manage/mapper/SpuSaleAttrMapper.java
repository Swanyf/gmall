package com.swan.gmall.manage.mapper;

import com.swan.gmall.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectSaleAttrValueList(@Param("spuId") String spuId, @Param("skuId") String skuId);
}
