package com.swan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.bean.BaseSaleAttr;
import com.swan.gmall.bean.SpuInfo;
import com.swan.gmall.manage.util.UploadFileUtil;
import com.swan.gmall.service.BaseAttrService;
import com.swan.gmall.service.SpuManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SpuManageController {

    @Reference
    SpuManageService spuManageService;

    @Reference
    BaseAttrService baseAttrService;

    @RequestMapping("spu/manage/page")
    public String toSpuManagerPage() {
        return "spu_manage_page";
    }

    // 获取spu_info表的数据
    @RequestMapping("get/spu/items/by/catalog3Id")
    @ResponseBody
    public List<SpuInfo> getSpuItems(@RequestParam("catalog3Id") String catalog3Id){
        return spuManageService.getSpuItems(catalog3Id);
    }

    // 获取base_sale_attr的销售属性
    @RequestMapping("get/sale/attr/name")
    @ResponseBody
    public List<BaseSaleAttr> getSaleAttrName(){
        return baseAttrService.getSaleAttrName();
    }

    // 保存spu_info的spuSaleAttrList数据
    @RequestMapping("save/spu/info")
    @ResponseBody
    public String saveSpuInfo(SpuInfo spuInfo){
        spuManageService.saveSpuInfo(spuInfo);
        return "操作成功！";
    }

    // 上传图片测试
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        return UploadFileUtil.imgUpload(multipartFile);
    }

}
