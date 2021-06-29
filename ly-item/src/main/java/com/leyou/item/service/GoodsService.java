package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.*;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品业务Service
 */
@Service
@Transactional
public class GoodsService extends ServiceImpl<SkuMapper, Sku> {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService cateogroyService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public PageResult<SpuDTO> spuPageQuery(Integer page, Integer rows, String key, Boolean saleable) {
        //1.封装条件
        //1.1 分页参数
        IPage<Spu> iPage = new Page<>(page,rows);

        //1.2 查询条件
        QueryWrapper<Spu> queryWrapper = Wrappers.query();

        //处理key
        if(StringUtils.isNotEmpty(key)){
            //注意：MyBatis-Plus如何把一些条件作为一个整体去执行： 使用QueryWrapper的and方法
            //where (name like '%key%' or sub_title like '%key%')

            queryWrapper.and(
                    i->i.like("name",key)
                            .or()
                            .like("sub_title",key)
            );
        }

        //处理saleable
        if(saleable!=null){
            queryWrapper.eq("saleable",saleable);
        }

        //2.执行查询，获取结果(Spu)
        iPage  = spuMapper.selectPage(iPage,queryWrapper);

        //3.处理结果，返回结果
        //3.1 取出Spu列表
        List<Spu> spuList = iPage.getRecords();
        //3.2 把Spu的集合转换SpuDTO的集合
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(spuList,SpuDTO.class);
        //3.3 封装分类名称和品牌名称
        getCategoryNameAndBrandName(spuDTOList);
        //3.4 封装PageResult
        PageResult<SpuDTO> pageResult = new PageResult<>(iPage.getTotal(),iPage.getPages(),spuDTOList);
        //3.5 返回数据
        return pageResult;
    }

    /**
     * 封装分类名称和品牌名称
     * @param spuDTOList
     */
    public void getCategoryNameAndBrandName(List<SpuDTO> spuDTOList) {

        //遍历集合
        spuDTOList.forEach(spuDTO -> {

            //1.封装品牌
            Brand brand = brandService.findBrandById(spuDTO.getBrandId());
            spuDTO.setBrandName(brand.getName());

            //2.封装分类
            List<Category> categoryList = cateogroyService.findCategoriesByCids(Arrays.asList(spuDTO.getCid1(), spuDTO.getCid2(), spuDTO.getCid3()));

            /**
             * JDK1.8新特性
             */

            String categoryName = categoryList.stream().map(Category::getName).collect(Collectors.joining("/"));//格式： 手机/手机通讯/手机
            spuDTO.setCategoryName(categoryName);
        });

    }

    public void saveGoods(SpuDTO spuDTO) {
        try {
            //1.保存Spu表
            //拷贝数据
            Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
            //默认下架
            spu.setSaleable(false);
            spuMapper.insert(spu);

            //2.保存spu_detail表
            SpuDetail spuDetail = spuDTO.getSpuDetail();
            spuDetail.setSpuId(spu.getId());
            spuDetailMapper.insert(spuDetail);

            //3.保存sku表
            List<Sku> skus = spuDTO.getSkus();
            //设置spuId
            skus.forEach(sku -> {
                sku.setSpuId(spu.getId());
            });
            //批量保存
            saveBatch(skus);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public void updateSaleable(Long id, Boolean saleable) {
        try {
            Spu spu = new Spu();
            spu.setId(id);
            spu.setSaleable(saleable);
            spuMapper.updateById(spu);

            //1）根据saleable确定主题标签
            String tagName = saleable? MQConstants.Tag.ITEM_UP_TAG : MQConstants.Tag.ITEM_DOWN_TAG;
            String destination = MQConstants.Topic.ITEM_TOPIC_NAME+":"+tagName;

            //2)发送消息给RocketMQ
            /**
             * 参数一：目的地。只写Topic，也可以写Topic:Tag
             * 参数二：消息内容
             */
            rocketMQTemplate.convertAndSend(destination,id);

        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    public List<Sku> findSkusBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        QueryWrapper<Sku> queryWrapper = Wrappers.query(sku);
        List<Sku> skus = skuMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return skus;
    }

    public SpuDetail findSpuDetailBySpuId(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectById(id);
        if(spuDetail==null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return spuDetail;

    }

    public SpuDTO findSpuDTOBySpuId(Long id) {
        //1.查询Spu对象
        Spu spu = spuMapper.selectById(id);
        //2.拷贝数据
        SpuDTO spuDTO = BeanHelper.copyProperties(spu, SpuDTO.class);
        //3.查询SpuDetail并封装
        SpuDetail spuDetail = spuDetailMapper.selectById(id);
        spuDTO.setSpuDetail(spuDetail);
        //4.查询Spu下的Sku对象
        List<Sku> skus = findSkusBySpuId(id);
        spuDTO.setSkus(skus);
        return spuDTO;
    }

    public List<Sku> findSkusByIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectBatchIds(ids);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return skus;
    }

    public void minusStock(Map<Long, Integer> paramMap) {
        paramMap.entrySet().forEach(entry->{
            Long skuId = entry.getKey();
            Integer num = entry.getValue();

            //查询原库存
            Sku sku = skuMapper.selectById(skuId);
            //判断库存是否足够
            if(sku.getStock()>=num){
                //扣减库存
                sku.setStock(sku.getStock()-num);
                //更新库存
                skuMapper.updateById(sku);
            }
        });
    }
}
