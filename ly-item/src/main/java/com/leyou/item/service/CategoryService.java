package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类Service
 */
@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> findCategoriesByPid(Long pid) {
        //1.封装查询条件
        //QueryWrapper: 封装查询条件的对象
        /**
         * query(): 用于复杂查询（不是多表查询，只是单表复杂查询，例如：分页，带条件的分页，带排序的分页）
         * query(T): 用于简单查询（普通的单表的条件  例如：根据名称查询，根据父id查询）
         */
        Category category = new Category();
        category.setParentId(pid);

        QueryWrapper<Category> queryWrapper = Wrappers.query(category);

        //2.执行查询，获取结果
        List<Category> categories = categoryMapper.selectList(queryWrapper);

        //3.处理并返回结果
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    public List<Category> findCategoriesByCids(List<Long> ids) {
        //selectBatchIds: 根据id批量查询
        List<Category> categories = categoryMapper.selectBatchIds(ids);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }
}
