package com.swan.gmall.list.json;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import sun.applet.Main;

import java.util.List;

@Service
public class BaseCatalogServiceImpl implements BaseCatalogService{

    @Autowired
    BaseCatalogMapper baseCatalogMapper;

    @Override
    public List<BaseCatalog1> getAllCatalog() {
        List<BaseCatalog1> allCatalog = baseCatalogMapper.getAllCatalog();
        return allCatalog;
    }


}
