package com.swan.gmall.list.json;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BaseCatalogService {

    List<BaseCatalog1> getAllCatalog();
}
