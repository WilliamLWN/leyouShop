package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    void saveCategoryAndBrand(@Param("bid") Long id, @Param(("cids")) List<Long> cids);

    @Select("SELECT b.* FROM tb_brand b,tb_category_brand cb WHERE b.id=cb.`brand_id` AND cb.`category_id` = #{id}")
    List<Brand> findBrandsByCid(Long id);
}