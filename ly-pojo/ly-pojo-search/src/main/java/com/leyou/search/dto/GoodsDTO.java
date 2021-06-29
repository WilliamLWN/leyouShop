package com.leyou.search.dto;

import lombok.Data;

/**
 * 只封装商品搜索页显示商品所需要的信息
 */
@Data
public class GoodsDTO {
    private Long id;
    private String spuName;
    private String subTitle;
    private String skus;
}
