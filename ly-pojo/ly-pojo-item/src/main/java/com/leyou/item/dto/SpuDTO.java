package com.leyou.item.dto;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

/**
 * 封装查询Spu数据结果 和 接收页面Spu数据
 */
@Data
public class SpuDTO extends Spu {
    private String categoryName;//分类名称，格式：手机/手机通讯/手机
    private String brandName;//品牌名称，格式：华为

    private SpuDetail spuDetail; //用于接收添加商品的SpuDetail数据
    private List<Sku> skus; // 用于接收添加商品的SpuDetail数据
}