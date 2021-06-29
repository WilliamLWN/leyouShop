package com.leyou.item.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.pojo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("item-service") //通过nacos查找服务名称，来进行远程调用
public interface ItemClient {

    /**
     * 分页查询商品
     */
    @GetMapping("/spu/page")
    public PageResult<SpuDTO> spuPageQuery(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable
    );


    /**
     * 根据spuId查询Sku集合
     */
    @GetMapping("/sku/of/spu")
    public List<Sku> findSkusBySpuId(@RequestParam("id") Long id);

    /**
     * 查询规格参数
     */
    @GetMapping("/spec/params")
    public List<SpecParam> findSpecParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value="cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    /**
     * 根据spuId查询SpuDetail
     */
    @GetMapping("/spu/detail")
    public SpuDetail findSpuDetailBySpuId(@RequestParam("id") Long id);

    /**
     * 根据分类ID查询分类集合
     */
    @GetMapping("/category/list")
    public List<Category> findCategoriesByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 根据品牌Id集合查询品牌对象集合
     */
    @GetMapping("/brand/list")
    public List<Brand> findBrandsByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 根据spuId查询SpuDTO对象
     */
    @GetMapping("/spu/{id}")
    public SpuDTO findSpuDTOById(@PathVariable("id") Long id);

    /**
     * 根据id查询品牌
     */
    @GetMapping("/brand/{id}")
    public Brand findBrandById(@PathVariable("id") Long id);

    /**
     * 根据分类ID查询规格参数组（包含组内参数）
     */
    @GetMapping("/spec/of/category")
    public List<SpecGroupDTO> findSpecGroupDTOByCid(@RequestParam("id") Long id);

    /**
     * 根据skuId集合查询Sku对象集合
     */
    @GetMapping("/sku/list")
    public List<Sku> findSkusByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 扣减库存
     */
    @PutMapping("/stock/minus")
    public Void minusStock(
            @RequestBody Map<Long,Integer> paramMap);
}
