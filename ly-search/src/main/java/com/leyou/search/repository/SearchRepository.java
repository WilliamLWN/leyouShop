package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 泛型一：操作的实体类对象
 * 泛型二：实体类的ID类型
 */
public interface SearchRepository extends ElasticsearchRepository<Goods,Long> {
}
