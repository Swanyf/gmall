package com.swan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.bean.BaseAttrInfo;
import com.swan.gmall.bean.SkuInfo;
import com.swan.gmall.bean.SpuImage;
import com.swan.gmall.bean.SpuSaleAttr;
import com.swan.gmall.service.BaseCatalogService;
import com.swan.gmall.service.SkuManagerService;
import com.swan.gmall.service.SpuManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.image.PackedColorModel;
import java.util.List;

@Controller
public class SkuManageController {

    @Reference
    SkuManagerService skuManagerService;

    @Reference
    BaseCatalogService baseCatalogService;


    @Reference
    SpuManageService spuManageService;

    // 获取skulist
    @RequestMapping("get/sku/info/list")
    @ResponseBody
    public List<SkuInfo> getSkuInfoList(@RequestParam("spuId") String spuId){
        return skuManagerService.getSkuInfoList(spuId);
    }

    // 获取平台属性
    @RequestMapping("get/attr/info")
    @ResponseBody
    public List<BaseAttrInfo> getAttrInfo(@RequestParam("catalog3Id") String catalog3Id){
        List<BaseAttrInfo> attrInfoList = baseCatalogService.getAttrInfoByCatalog3Id(catalog3Id);
        return attrInfoList;
    }

    // 获取销售属性
    @RequestMapping("get/sale/attr")
    @ResponseBody
    public List<SpuSaleAttr> getSaleAttr(@RequestParam("spuId") String spuId) {
        List<SpuSaleAttr> saleAttr = spuManageService.getSaleAttr(spuId);
        return saleAttr;
    }

    // 获取图片列表
    @RequestMapping("get/spu/img/list")
    @ResponseBody
    public List<SpuImage> getSpuImgList(@RequestParam("spuId") String spuId){
        List<SpuImage> spuImgList = spuManageService.getSpuImgList(spuId);
        return spuImgList;
    }

    // 保存sku商品信息
    @RequestMapping("save/sku/info")
    @ResponseBody
    public String saveSkuInfo(SkuInfo skuInfo){
        skuManagerService.saveSkuInfo(skuInfo);
        return "操作成功！";
    }


}
