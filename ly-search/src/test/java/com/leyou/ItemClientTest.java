package com.leyou;

import com.leyou.item.client.ItemClient;
import com.leyou.item.pojo.Sku;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Feign接口测试
 */
@RunWith(SpringRunner.class) //加载Junit运行环境
@SpringBootTest(classes = LySearchApplication.class)  //读取SpringBoot启动类
public class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Test
    public void testFindSkusBySpuId(){
        List<Sku> skus = itemClient.findSkusBySpuId(3L);
        skus.forEach(System.out::println);
    }


}

