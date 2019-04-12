package com.swan.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.swan.gmall.bean.SkuLsInfo;
import com.swan.gmall.bean.SkuLsParam;
import com.swan.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.Highlighter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.util.*;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    JestClient jestClient;

    @Override
    public List<SkuLsInfo> getSkuLsInfos(SkuLsParam skuLsParam) {

        // 获取查询结果的对象
        String gmallDSL = gmallDSL(skuLsParam);
        System.out.println(gmallDSL);

        // 将skuLsInfo数据导入到es中
        Search search = new Search.Builder(gmallDSL)    // 要导入的对象
                .addIndex("gmall")  // 导入到哪个索引库
                .addType("SkuLsInfo")   // 导入到哪张表中
                .build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 用于存放封装后的es数据
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();

        // 校验
        if (execute.getTotal() > 0) {
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
            // 获取es中的查询结果的数据并封装到SkuLsInfo的集合对象中
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if (highlight != null) {
                    List<String> skuName = highlight.get("skuName");
                    String s = skuName.get(0);
                    source.setSkuName(s);
                }
                skuLsInfos.add(source);
            }
        }

        // 取聚合值
        List<String> attrValueIdList = new ArrayList<>();
        MetricAggregation aggregations = execute.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("valueIdAggs");
        if (groupby_attr != null) {
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add(bucket.getKey());
            }
        }

        return skuLsInfos;
    }

    public String gmallDSL(SkuLsParam skuLsParam) {

        // 创建查询的对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 创建联合查询的对象
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 获取请求的三级分类id,根据请求的三级分类id查询
        String catalog3Id = skuLsParam.getCatalog3Id();

        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 根据属性值id查询
        String[] valueIds = skuLsParam.getValueId();
        if (valueIds != null && valueIds.length > 0) {
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        // 根据关键字查询
        String keyword = skuLsParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        // 高亮显示查询后的skuName
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        // 查询数量
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);

        // 聚合属性值id
        TermsBuilder valueIdAggs = AggregationBuilders.terms("valueIdAggs").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(valueIdAggs);

        return searchSourceBuilder.toString();
    }

    public static void main(String[] args) {
        List<String> list = Arrays.asList("asd", "asdsd", "wqe", "gfdg","wqer");
        //System.out.println(list.get(3));

        for (String s : list) {
            boolean wqe = s.contains("wqe");
            if (wqe == true) {
                System.out.println(s);
            }
        }




    }


}


