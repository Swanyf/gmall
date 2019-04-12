package com.swan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.bean.BaseAttrInfo;
import com.swan.gmall.bean.BaseAttrValue;
import com.swan.gmall.service.BaseAttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BaseAttrController {

    @Reference
    BaseAttrService attrService;

    @RequestMapping("save/attr/list")
    @ResponseBody
    public String saveAttrList(BaseAttrInfo baseAttrInfo){
        try {
            attrService.saveAttrList(baseAttrInfo);
            return "保存成功！";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存失败！");
        }
    }

    // 根据id查询多个属性值名称
    @RequestMapping("get/value/name/list")
    @ResponseBody
    public List<BaseAttrValue> getValueNameList(@RequestParam("id") String id){
        return attrService.getValueNameList(id);
    }

    // 根据id删除base_arrt_value表的行信息
    @RequestMapping("remoce/attr/value/name")
    @ResponseBody
    public String removeAttrValueName(@RequestParam("id") String id){
        attrService.removeAttrValueNameById(id);
        return "删除成功";
    }

    @RequestMapping("save/attr/info")
    @ResponseBody
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo){
        attrService.saveAttrInfo(baseAttrInfo);
        return "操作成功";
    }

}
