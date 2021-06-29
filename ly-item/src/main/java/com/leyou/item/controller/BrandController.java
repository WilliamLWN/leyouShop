package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌
 */
@RestController
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     */
    @GetMapping("/brand/page")
    public ResponseEntity<PageResult<Brand>> brandPageQuery(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc
    ){
        PageResult<Brand> pageResult = brandService.brandPageQuery(page,rows,key,sortBy,desc);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增品牌
     *  1. 控制器使用对象来接收参数的情况
     *    1.1 普通参数，如：/brand?name=xxx&image=xxx&letter=xx 或 post正文name=xxx&image=xxx&letter=xx
     *          Brand brand
     *    1.2 Json参数，如：post正文 {name:xxx,image:xxx,letter:xx}
     *          @RequestBody Brand brand
     *
     *  2 接收多个同名参数的情况
     *     页面：
     *         1）提交表单多个复选框(checkbox) cids   cids=1&cids=2&cids=3
     *         2) 自定义拼接：cids=1,2,3
     *
     *     控制器：
     *        1）字符串：  String cids;   1,2,3
     *        2）数组：  Long[] cids;    [1,2,3]
     *        2）集合：  List<Long> cids;    [1,2,3]  注意：必须加上@RequestParam注解
     */
    @PostMapping("/brand")
    public ResponseEntity<Void> saveBrand(
            Brand brand,
            @RequestParam("cids") List<Long> cids
    ){
        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id查询品牌
     */
    @GetMapping("/brand/{id}")
    public ResponseEntity<Brand> findBrandById(
            @PathVariable("id") Long id
    ){
        Brand brand = brandService.findBrandById(id);
        return ResponseEntity.ok(brand);
    }

    /**
     * 根据分类ID查询品牌
     */
    @GetMapping("/brand/of/category")
    public ResponseEntity<List<Brand>> findBrandsByCid(
            @RequestParam("id") Long id
    ){
        List<Brand> brands = brandService.findBrandsByCid(id);
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据品牌Id集合查询品牌对象集合
     */
    @GetMapping("/brand/list")
    public ResponseEntity<List<Brand>> findBrandsByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> brands = brandService.findBrandsByIds(ids);
        return ResponseEntity.ok(brands);
    }
}
