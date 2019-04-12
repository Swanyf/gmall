package com.swan.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.swan.gmall.bean.*;
import com.swan.gmall.service.BaseAttrService;
import com.swan.gmall.service.ListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;

@Controller
public class ListController {

    @Reference
    ListService listService;

    @Reference
    BaseAttrService baseAttrService;

    @RequestMapping("list.html")
    // 入参的对象(也就是查询各种条件)
    public String toListPage(SkuLsParam skuLsParam, Model model) {

        /*
            根据入参的对象查询从es中封装好的数据
         */
        List<SkuLsInfo> skuLsInfos = listService.getSkuLsInfos(skuLsParam);
        model.addAttribute("skuLsInfoList", skuLsInfos);

        /*
            显示分类列表
         */

        Set<String> setValueId = new HashSet<>();

        for (SkuLsInfo skuLsInfo : skuLsInfos) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();

            // 遍历获取到valueId
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                setValueId.add(valueId);
            }
        }

        // 分割valueId
        String join = StringUtils.join(setValueId, ",");
        // 根据join查询属性和属性值表
        List<BaseAttrInfo> attrInfos = baseAttrService.getAttrInfosByJoin(join);

        // 将结果集保存到域中
        model.addAttribute("attrList", attrInfos);


        /*
            去掉列表中重复提交的属性
        */

        // 获取当前请求参数中的valueId
        String[] valueIds = skuLsParam.getValueId();

        List<AttrValueCrumb> attrValueCrumbList = new ArrayList<>();
        if (valueIds != null && valueIds.length > 0) {
            // 遍历属性列表
            Iterator<BaseAttrInfo> iterator = attrInfos.iterator();

            while (iterator.hasNext()) {
                List<BaseAttrValue> attrValueList = iterator.next().getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    // 获取列表中的valueId
                    String baseAttrValueId = baseAttrValue.getId();
                    // 遍历当前请求参数中的valueId
                    for (String valueId : valueIds) {
                        if (baseAttrValueId.equals(valueId)) {
                            // 当属性列表中消失一个属性就添加一个面包屑
                            AttrValueCrumb attrValueCrumb = new AttrValueCrumb();
                            String urlParam = getMyCrumbUrlParam(skuLsParam, valueId);
                            // 设置地址
                            attrValueCrumb.setUrlParam(urlParam);
                            // 设置属性值
                            attrValueCrumb.setValueName(baseAttrValue.getValueName());

                            attrValueCrumbList.add(attrValueCrumb);
                            // 如果相等，移除属性列表中的valueId
                            iterator.remove();
                        }
                    }
                }
            }
        }

        model.addAttribute("attrValueSelectedList", attrValueCrumbList);

        /*
            根据入参对象skuLsParam的各属性拼接得到get请求参数
        */

        String urlParam = getUrlParam(skuLsParam);
        model.addAttribute("urlParam", urlParam);
        return "list";
    }

    private String getMyCrumbUrlParam(SkuLsParam skuLsParam, String... crumbValueId) {

        String urlParam = "";
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }

        if (valueId != null && valueId.length > 0) {
            for (String id : valueId) {
                if (crumbValueId != null && !crumbValueId[0].equals(id)) {
                    urlParam = urlParam + "&valueId=" + id;
                }
            }
        }

        return urlParam;
    }

    private String getUrlParam(SkuLsParam skuLsParam) {
        String urlParam = "";
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueIds = skuLsParam.getValueId();

        // 校验
        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + catalog3Id;
        }

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        }


        if (valueIds != null && valueIds.length > 0) {
            for (String valueId : valueIds) {
                urlParam += "&valueId=" + valueId;
            }
        }

        return urlParam;

    }

    public static int test(int n){
        if (n == 1 || n == 2) {
            return n;
        }

        return test(n-2) + test(n-1);

    }
       //static int i = 1;

    public static void main(String[] args) {
        String s1 = "ab";
        String s2 = "a" + "b";
        String s3 = "a";
        String s4 = "b",s5 = s3 + s4,s6 = "a";
        System.out.println(s5 == s2);
        System.out.println(s2);
        System.out.println(s5);

    }
}
