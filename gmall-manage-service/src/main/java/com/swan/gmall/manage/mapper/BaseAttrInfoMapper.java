package com.swan.gmall.manage.mapper;

import com.swan.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {

    List<BaseAttrInfo> selectAttrInfosByJoin(@Param("join") String join);
}
