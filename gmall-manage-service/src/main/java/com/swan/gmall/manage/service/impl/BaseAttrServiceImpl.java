package com.swan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.swan.gmall.bean.BaseAttrInfo;
import com.swan.gmall.bean.BaseAttrValue;
import com.swan.gmall.bean.BaseSaleAttr;
import com.swan.gmall.manage.mapper.BaseAttrInfoMapper;
import com.swan.gmall.manage.mapper.BaseAttrValueMapper;
import com.swan.gmall.manage.mapper.BaseSaleAttrMapper;
import com.swan.gmall.service.BaseAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BaseAttrServiceImpl implements BaseAttrService {

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Override
    public void saveAttrList(BaseAttrInfo baseAttrInfo) {
        // 先保存attrName
        baseAttrInfoMapper.insertSelective(baseAttrInfo);

        // 插入数据后返回一个主键id
        String attrInfoId = baseAttrInfo.getId();
        // 根据主键id获取attrValue的集合
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(attrInfoId);
            // 执行保存attrValue
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }
    }

    @Override
    public List<BaseSaleAttr> getSaleAttrName() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public List<BaseAttrValue> getValueNameList(String id) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(id);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.select(baseAttrValue);
        return baseAttrValues;
    }

    @Override
    public void removeAttrValueNameById(String id) {
        baseAttrValueMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //如果有主键就进行更新，如果没有就插入
        if (baseAttrInfo.getId() != null && baseAttrInfo.getId().length() > 0) {
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        } else {
            //防止主键被赋上一个空字符串
            if (baseAttrInfo.getId().length() == 0) {
                baseAttrInfo.setId(null);
            }
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        //把原属性值全部清空
        BaseAttrValue baseAttrValue4Del = new BaseAttrValue();
        baseAttrValue4Del.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValue4Del);

        //重新插入属性
        if (baseAttrInfo.getAttrValueList() != null && baseAttrInfo.getAttrValueList().size() > 0) {
            for (BaseAttrValue attrValue : baseAttrInfo.getAttrValueList()) {
                //防止主键被赋上一个空字符串
                if (attrValue.getId() != null && attrValue.getId().length() == 0) {
                    attrValue.setId(null);
                }
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }
    }

    @Override
    public List<BaseAttrInfo> getAttrInfosByJoin(String join) {
        return baseAttrInfoMapper.selectAttrInfosByJoin(join);
    }

    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 3, 4, 6, 5, 8, 2};
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 0; j < nums.length-1; j++) {
                if (nums[j] > nums[j+1]) {
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j+1] = temp;
                }
            }
        }
        for (int i = 0; i < nums.length; i++) {
            System.out.println(nums[i]);
        }

    }
}


