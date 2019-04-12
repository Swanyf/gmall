package com.swan.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.swan.gmall.bean.SkuInfo;
import com.swan.gmall.bean.SkuSaleAttrValue;
import com.swan.gmall.bean.SpuSaleAttr;
import com.swan.gmall.service.SkuManagerService;
import com.swan.gmall.service.SpuManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuManagerService skuManagerService;

    @Reference
    SpuManageService spuManageService;

    @RequestMapping("{skuId}.html")
    public String toItemPage(@PathVariable("skuId") String skuId,Model model){
        /*
            显示页面商品的基本信息
         */
        SkuInfo skuItems = skuManagerService.getItems(skuId);
        String spuId = skuItems.getSpuId();
        model.addAttribute("skuItems",skuItems);

        /*
            显示页面该商品的销售属性和销售属性值
         */
        // 根据 spuId 查询该商品对应的销售属性和值
        List<SpuSaleAttr> saleAttrValueList = spuManageService.getSaleAttrValueList(spuId, skuId);
        model.addAttribute("spuSaleAttrListCheckBySku",saleAttrValueList);

        // 创建一个 map 集合用于存放 saleAttrValue 的 id 和 skuId
        HashMap<String, String> skuMap = new HashMap<>();
        // 根据商品 spuId 和选中的当前销售属性值 id 直接获取到 sku_info 信息
        List<SkuInfo> skuInfoListBySpuId = skuManagerService.getSkuInfoListBySpuId(spuId);

        for (SkuInfo skuInfo : skuInfoListBySpuId) {
            // 获取skuId
            String value = skuInfo.getId();

            String key = "";
            // 获取销售属性 saleAttrList 的数据
            List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                // 获取销售属性值
                String attrValueId = skuSaleAttrValue.getSaleAttrValueId();
                key += attrValueId + "&";
            }
            skuMap.put(key,value);
        }

        // 需要给浏览器返回json的字符串
        model.addAttribute("skuMap",JSON.toJSONString(skuMap));

        return "item";
    }

}
