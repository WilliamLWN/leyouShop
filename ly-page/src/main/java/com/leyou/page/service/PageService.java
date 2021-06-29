package com.leyou.page.service;

import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;
    @Value("${ly.static.itemDir}")
    private String itemDir;
    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> getDetailData(Long id) {
        Map<String, Object> resultMap = new HashMap<>();

        //1.根据spuId查询SpuDTO
        SpuDTO spuDTO = itemClient.findSpuDTOById(id);
        //2.根据分类ID集合查询分类对象集合
        List<Category> categoryList = itemClient.findCategoriesByIds(Arrays.asList(spuDTO.getCid1(), spuDTO.getCid2(), spuDTO.getCid3()));
        //3.根据品牌Id查询品牌对象
        Brand brand = itemClient.findBrandById(spuDTO.getBrandId());
        //4.根据分类Id查询规格参数组（包含组内参数）
        List<SpecGroupDTO> specGroupDTOList = itemClient.findSpecGroupDTOByCid(spuDTO.getCid3());

        resultMap.put("categories",categoryList);
        resultMap.put("brand",brand);
        resultMap.put("spuName",spuDTO.getName());
        resultMap.put("subTitle",spuDTO.getSubTitle());
        resultMap.put("detail",spuDTO.getSpuDetail());
        resultMap.put("skus",spuDTO.getSkus());
        resultMap.put("specs",specGroupDTOList);

        return resultMap;
    }

    /**
     * 为商品生产详情静态页
     */
    public void createStaticPage(Long id){
        //1.创建Context对象
        Context context = new Context();
        //设置动态数据
        context.setVariables(getDetailData(id));

        //2.读取模板页面（自动到resources/templates目录下读取）
        String templateName = "item.html";

        //3.模板引擎生产静态页面
        /**
         * 参数一：模板页面名称
         * 参数二：Context对象
         * 参数三：文件输出流
         */
        String fileName = id+".html";

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(itemDir ,fileName ));
            templateEngine.process(templateName,context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            //必须把输出流关闭，否则后面无法删除该文件
            writer.close();
        }
    }

    public void deleteStaticPage(Long spuId) {
        //1.读取静态页文件
        File file = new File(itemDir, spuId + ".html");
        //2.删除文件
        if (file.exists()) {
            file.delete();
        }
    }
}
