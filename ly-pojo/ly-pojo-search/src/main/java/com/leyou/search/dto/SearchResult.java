package com.leyou.search.dto;

import com.leyou.common.pojo.PageResult;
import lombok.Data;
import java.util.Map;

/**
 * 封装商品搜索的结果
 * @param <T>
 */
@Data
public class SearchResult<T> extends PageResult<T> {
    private Map<String,Object> filterConditions;
}
