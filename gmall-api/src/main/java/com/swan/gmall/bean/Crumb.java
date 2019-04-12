package com.swan.gmall.bean;

public class Crumb {

    private String valueName;// 当前请求的url中所包含的属性值

    private String urlParam;//当前请求的url减去面包屑的属性值的新的请求url

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }
}
