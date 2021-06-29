package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品Controller
 */
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询商品
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> spuPageQuery(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable
    ){
        PageResult<SpuDTO> pageResult = goodsService.spuPageQuery(page,rows,key,saleable);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 保存商品
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 商品上下架
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam("id") Long id,@RequestParam("saleable") Boolean saleable){
        goodsService.updateSaleable(id,saleable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuId查询Sku集合
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<Sku>> findSkusBySpuId(@RequestParam("id") Long id){
        List<Sku> skus = goodsService.findSkusBySpuId(id);
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据spuId查询SpuDetail
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetail> findSpuDetailBySpuId(@RequestParam("id") Long id){
        SpuDetail spuDetail = goodsService.findSpuDetailBySpuId(id);
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuId查询SpuDTO对象
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> findSpuDTOBySpuId(@PathVariable("id") Long id){
        SpuDTO spuDTO = goodsService.findSpuDTOBySpuId(id);
        return ResponseEntity.ok(spuDTO);
    }

    /**
     * 根据skuId集合查询Sku对象集合
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> findSkusByIds(@RequestParam("ids") List<Long> ids){
        List<Sku> skus = goodsService.findSkusByIds(ids);
        return ResponseEntity.ok(skus);
    }

    /**
     * 扣减库存
     */
    @PutMapping("/stock/minus")
    public ResponseEntity<Void> minusStock(
            @RequestBody Map<Long,Integer> paramMap
    ){
        goodsService.minusStock(paramMap);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

