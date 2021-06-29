package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller // 必须是Controller，不能@RestController
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 接收商品详情
     */
    @GetMapping("/item/{id}.html")
    public String pageDetail(@PathVariable("id") Long id, Model model){

        //调用业务层，获取数据
        Map<String,Object> resultlMap = pageService.getDetailData(id);

        //把数据存入Model
        model.addAllAttributes(resultlMap);

        //注意：thymealeaf默认读取resources下templates目录的文件,后缀名必须和.html
        return "item";
    }

}

