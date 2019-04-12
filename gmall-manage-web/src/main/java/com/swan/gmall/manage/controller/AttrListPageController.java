package com.swan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.bean.BaseAttrInfo;
import com.swan.gmall.bean.BaseCatalog1;
import com.swan.gmall.bean.BaseCatalog2;
import com.swan.gmall.bean.BaseCatalog3;
import com.swan.gmall.service.BaseCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AttrListPageController {

    @Reference
    BaseCatalogService baseCatalogService;

    @RequestMapping("attr/list/page")
    public String arrtListPage(){
        return "attr_list_page";
    }

    // 获取一级分类catalog1的表数据
    @RequestMapping("get/catalog1/items")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1Items(){
        return baseCatalogService.getCatalog1Items();
    }

    // 获取二级分类catalog2的表数据
    @RequestMapping("get/catalog2/items")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2Items(@RequestParam("catalog1Id") String catalog1Id){
        // 需要根据一级分类的id查询
        List<BaseCatalog2> catalog2Items = baseCatalogService.getCatalog2Items(catalog1Id);
        return catalog2Items;
    }

    // 获取三级分类catalog3的表数据
    @RequestMapping("get/catalog3/items")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3Items(@RequestParam("catalog2Id") String catalog2Id){
        // 需要根据二级分类的id查询
        return baseCatalogService.getCatalog3Items(catalog2Id);
    }

    // 根据三级分类的id查询属性名称
    @RequestMapping("get/attr/info/by/catalog3Id")
    @ResponseBody
    public List<BaseAttrInfo> getAttrInfoByCatalog3Id(@RequestParam("catalog3Id") String catalog3Id){
        return baseCatalogService.getAttrInfoByCatalog3Id(catalog3Id);
    }

    // 根据id删除属性
    @RequestMapping("remove/attr")
    @ResponseBody
    public String removeAttrByIdList(@RequestParam("id") String id){
        try {
            baseCatalogService.removeAttrByIdList(id);
            return "删除成功！";
        } catch (Exception e) {
            e.printStackTrace();
            return "删除失败！";
        }
    }

}
