package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> brandPageQuery(Integer page, Integer rows, String key, String sortBy, Boolean desc) {

        //1.封装条件
        //1.1 分页参数
        IPage<Brand> iPage = new Page<>(page,rows);

        //1.2 查询参数
        QueryWrapper<Brand> queryWrapper = Wrappers.query();

        //处理key
        if(StringUtils.isNotEmpty(key)){
            //name like '%xx%' or letter = xx
            queryWrapper.like("name",key)
                    .or()
                    .eq("letter",key.toUpperCase());
        }

        //处理排序
        if(StringUtils.isNotEmpty(sortBy)){
            if(desc){
                //降序
                queryWrapper.orderByDesc(sortBy);
            }else{
                //升序
                queryWrapper.orderByAsc(sortBy);
            }
        }

        //2.执行查询，获取结果
        iPage = brandMapper.selectPage(iPage,queryWrapper);

        //3.处理并返回结果
        //3.1 封装PageResult<Brand>
        PageResult<Brand> pageResult = new PageResult<Brand>(iPage.getTotal(),iPage.getPages(),iPage.getRecords());
        //3.2 返回数据
        return pageResult;
    }

    public void saveBrand(Brand brand, List<Long> cids) {
        try {
            //1.保存品牌表数据
            brandMapper.insert(brand);

            //2.保存分类品牌表数据
            brandMapper.saveCategoryAndBrand(brand.getId(),cids);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }

    public Brand findBrandById(Long id) {
        Brand brand = brandMapper.selectById(id);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> findBrandsByCid(Long id) {
        List<Brand> brands = brandMapper.findBrandsByCid(id);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }

    public List<Brand> findBrandsByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectBatchIds(ids);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brands;
    }
}

