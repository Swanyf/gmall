package com.swan.gmall.list;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.swan.gmall.bean.SkuInfo;
import com.swan.gmall.bean.SkuLsInfo;
import com.swan.gmall.list.json.BaseCatalog1;
import com.swan.gmall.list.json.BaseCatalogService;
import com.swan.gmall.service.SkuManagerService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    JestClient jestClient;

    @Reference
    SkuManagerService skuManagerService;

    @Test
    public void contextLoads() throws IOException {
        String gmallDsl = gmallDsl();
        System.out.println(gmallDsl);
        Search search = new Search.Builder(gmallDsl).addIndex("gmall").build();

        SearchResult execute = jestClient.execute(search);
        //execute.getHits(Moive)

    }

    public void setEs() throws IOException {
        // 查询skuInfo的数据
        List<SkuInfo> skuInfoList =  skuManagerService.getSkuInfoListByCatalog3Id("61");
        for (SkuInfo skuInfo : skuInfoList) {
            SkuLsInfo skuLsInfo = new SkuLsInfo();
            BeanUtils.copyProperties(skuInfo,skuLsInfo);

            // 将skuLsInfo数据导入到es中
            Index build = new Index.Builder(skuLsInfo)    // 要导入的对象
                    .index("gmall")     // 导入到哪个索引库
                    .type("SkuLsInfo")     // 导入到哪张表中
                    .id(skuLsInfo.getId())  //设置主键索引
                    .build();

            jestClient.execute(build);
        }

    }

    // 一般用工具方法生成elasticsearch的增删改查
    public String gmallDsl() {
        // 创建一个查询对象(查询语句的封装)
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 联合查询
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder skuName = new MatchQueryBuilder("skuName", "0906");
        queryBuilder.must(skuName);

        sourceBuilder.query(queryBuilder);

        return sourceBuilder.toString();
    }

    @Reference
    BaseCatalogService baseCatalogService;

    @Test
    public void test3() throws IOException {
        List<BaseCatalog1> allCatalog = baseCatalogService.getAllCatalog();
        String jsonString = JSON.toJSONString(allCatalog);
        System.err.println(jsonString);

        File file = new File("d://catalog.txt");
        byte[] bytes = new byte[1024 * 10];

        FileOutputStream outputStream = new FileOutputStream(file);

        outputStream.write(jsonString.getBytes());
        outputStream.close();

       /* File fp=new File("/catalog.txt");
        String str="ABCDE";
        PrintWriter pfp= new PrintWriter(fp);
        pfp.print(str);
        pfp.close();*/
   }

}

