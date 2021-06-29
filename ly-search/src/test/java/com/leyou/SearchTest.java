package com.leyou;

import com.leyou.item.client.ItemClient;
import com.leyou.item.pojo.Sku;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class) //加载Junit运行环境
@SpringBootTest(classes = LySearchApplication.class)  //读取SpringBoot启动类
public class SearchTest {

    @Autowired
    private SearchService searchService;

    @Test
    public void testImportData(){
        searchService.importData();
    }


}
