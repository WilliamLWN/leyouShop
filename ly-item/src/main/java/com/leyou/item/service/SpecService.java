package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecParam> findSpecParams(Long gid, Long cid, Boolean searching) {

        //1.封装条件
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        //注意：MyBatis-Plus在底层执行条件查询的过程中，根据传入对象的属性是否为NULL来决定是否添加该条件

        QueryWrapper<SpecParam> queryWrapper = Wrappers.query(specParam);

        //2.执行查询，获取结果
        List<SpecParam> specParams = specParamMapper.selectList(queryWrapper);

        //3.处理并返回结果
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return specParams;
    }

    public List<SpecGroup> findSpecGroups(Long cid) {

        //1.封装条件
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        //注意：MyBatis-Plus在底层执行条件查询的过程中，根据传入对象的属性是否为NULL来决定是否添加该条件
        QueryWrapper<SpecGroup> queryWrapper = Wrappers.query(specGroup);

        //2.执行查询，获取结果
        List<SpecGroup> specGroups = specGroupMapper.selectList(queryWrapper);

        //3.处理并返回结果
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return specGroups;
    }

    public List<SpecGroupDTO> findSpecGroupDTOByCid(Long id) {
        //1.根据分类ID查询规格组
        List<SpecGroup> specGroups = findSpecGroups(id);
        //2.拷贝数据
        List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(specGroups, SpecGroupDTO.class);
        //3.封装每个参数组内的参数
        specGroupDTOS.forEach(specGroupDTO -> {
            //1)根据参数组ID查询参数
            List<SpecParam> specParams = findSpecParams(specGroupDTO.getId(), null, null);
            specGroupDTO.setParams(specParams);
        });
        return specGroupDTOS;
    }
}