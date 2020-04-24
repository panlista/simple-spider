package com.panlista.spider.service;

import com.google.common.collect.Lists;
import com.panlista.spider.constant.SysConstant;
import com.panlista.spider.entity.GoodsInfo;
import com.panlista.spider.mapper.GoodsInfoMapper;
import com.panlista.spider.util.HttpClientUtils;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import javax.annotation.Resource;

@Service
public class SpiderService {
    private static Logger logger = LoggerFactory.getLogger(SpiderService.class);

    @Resource
    private GoodsInfoMapper goodsInfoMapper;

    private static String HTTPS_PROTOCOL = "https:";

    static Map<String, String> headers;

    static {
        headers = new HashMap<>();
        //冒充浏览器
        headers.put(SysConstant.Header.USER_AGENT,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36");
    }

    public void spiderData(String url, Map<String, String> params) {
        String html = HttpClientUtils.sendGet(url, headers, params);
        if(!StringUtils.isBlank(html)) {
            List<GoodsInfo> goodsInfos =parseHtml(html);
//            goodsInfoDao.saveBatch(goodsInfos);
            goodsInfoMapper.insertForeach(goodsInfos);
        }
    }

    /**
     * 解析html
     * @param html
     */
    private List<GoodsInfo> parseHtml(String html) {
        //商品集合
        List<GoodsInfo> goods = Lists.newArrayList();
        /**
         * 获取dom并解析
         */
        Document document = Jsoup.parse(html);
        Elements elements = document.
                select("ul[class=gl-warp clearfix]").select("li[class=gl-item]");
        int index = 0;
        for(Element element : elements) {
            String goodsId = element.attr("data-sku");
            String goodsName = element.select("div[class=p-name p-name-type-2]").select("em").text();
            String goodsPrice = element.select("div[class=p-price]").select("strong").select("i").text();
            String imgUrl = HTTPS_PROTOCOL + element.select("div[class=p-img]").select("a").select("img").attr("src");
            GoodsInfo goodsInfo = new GoodsInfo(goodsId, goodsName, imgUrl, goodsPrice);
            goods.add(goodsInfo);
            String jsonStr = JSON.toJSONString(goodsInfo);
            logger.info("成功爬取【" + goodsName + "】的基本信息 ");
            logger.info(jsonStr);
            if(index ++ == 9) {
                break;
            }
        }
        return goods;
    }

}
