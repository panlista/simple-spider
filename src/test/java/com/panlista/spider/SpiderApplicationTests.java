package com.panlista.spider;

import com.panlista.spider.mapper.GoodsInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SpiderApplicationTests {

    @Resource
    GoodsInfoMapper goodsInfoMapper;

    @Test
    void contextLoads() {
        System.out.println(goodsInfoMapper);
    }

}
