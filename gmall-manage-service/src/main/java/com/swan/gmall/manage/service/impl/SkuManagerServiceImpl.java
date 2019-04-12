package com.swan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.swan.gmall.bean.SkuAttrValue;
import com.swan.gmall.bean.SkuImage;
import com.swan.gmall.bean.SkuInfo;
import com.swan.gmall.bean.SkuSaleAttrValue;
import com.swan.gmall.manage.mapper.SkuAttrValueMapper;
import com.swan.gmall.manage.mapper.SkuImageMapper;
import com.swan.gmall.manage.mapper.SkuInfoMapper;
import com.swan.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.swan.gmall.service.SkuManagerService;
import com.swan.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class SkuManagerServiceImpl implements SkuManagerService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<SkuInfo> getSkuInfoList(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        return skuInfoMapper.select(skuInfo);
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        String skuId = skuInfo.getId();

        // 保存图片
        // 遍历获取每一行图片数据
        for (SkuImage skuImage : skuImageList) {
            // 获取持久化保存sku_info表的id
            skuImage.setSkuId(skuId);
            skuImageMapper.insertSelective(skuImage);
        }

        // 保存平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insertSelective(skuAttrValue);
        }

        // 保存销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }

    }

    // 从数据库DB中取出数据
    @Override
    public SkuInfo getItemsFromDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        if (skuInfo == null) {
            return null;
        }

        // 查询图片信息
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);

        // 查询销售属性、属性值
        SkuSaleAttrValue saleAttrValue = new SkuSaleAttrValue();
        saleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(saleAttrValue);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);

        return skuInfo;
    }

    @Override
    public SkuInfo getItems(String skuId) {
        SkuInfo skuInfo = null;

        Jedis jedis = redisUtil.getJedis();
        String skuInfoStr = jedis.get("sku:" + skuId + ":info");
        // 将redis格式的数据转换成json对象
        skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);

        // 如果从 redis 缓存中取出的 skuInfo 数据不存在
        if (skuInfo == null) {
            /*
                获取当前的分布式锁
                    nx 键不存在时设置
                    px 键的过期时间
            */
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10000);

            // 如果输入参数String为null则不会抛出NullPointerException，
            // 而是做了相应处理，例如，如果输入为null则返回也是null
            if (StringUtils.isBlank(OK)) {
                // 如果没有设置成功，说明缓存锁被占用，等一会儿再继续申请
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 3秒之后重新根据 skuId申请 spuInfo 的信息
                return getItems(skuId);
            } else {
                // 拿到缓存锁，就从数据库中取出 skuInfo 数据
                skuInfo = getItemsFromDB(skuId);
            }

            // 并给 redis 中同步 skuInfo 的数据
            jedis.set("sku:" + skuId + ":info",JSON.toJSONString(skuInfo));
        }

        jedis.close();
        return skuInfo;
    }

    public static void main(String[] args) {
        String str = "s tr ing";
        for (int i = 0; i < str.length(); i++) {
            if (String.valueOf(str.charAt(i)) == " ") {
                String s = String.valueOf(str.charAt(i));
                s = "";
            }
            System.out.println(str);
        }
    }

    @Override
    public List<SkuInfo> getSkuInfoListBySpuId(String spuId) {
        return skuInfoMapper.selectSkuInfoListBySpuId(spuId);
    }

    @Override
    public List<SkuInfo> getSkuInfoListByCatalog3Id(String catalog3Id) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfoList = skuInfoMapper.select(skuInfo);
        for (SkuInfo info : skuInfoList) {
            SkuAttrValue saleAttrValue = new SkuAttrValue();
            saleAttrValue.setSkuId(info.getId());
            List<SkuAttrValue> saleAttrValues = skuAttrValueMapper.select(saleAttrValue);
            info.setSkuAttrValueList(saleAttrValues);
        }
        return skuInfoList;

    }

    @Override
    public SkuInfo getSkuInfoById(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        return skuInfo;
    }
}
