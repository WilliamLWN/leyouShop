package com.leyou.search.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.HighlightUtils;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.pojo.*;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.dto.SearchResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.SearchRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索业务
 */
@Service
public class SearchService {
    @Autowired
    private SearchRepository searchRepository; //完成基本CRUD操作
    @Autowired
    private ElasticsearchTemplate esTemplate;//用于高级搜索
    @Autowired
    private ItemClient itemClient;

    /**
     * 批量导入数据到ES的方法
     */
    public void importData(){
        int page = 1;//页码
        int rows = 100;//每次查询行数

        long totalPage = 1;//总页数
        //查询Spu数据
        do {
            PageResult<SpuDTO> pageResult = itemClient.spuPageQuery(page, rows, null, true);

            //取出当前页的所有SpuDTO
            List<SpuDTO> spuDTOList = pageResult.getItems();

            //把SpuDTO转换为Goods对象
            List<Goods> goodsList = spuDTOList.stream().map(spuDTO -> buildGoods(spuDTO)).collect(Collectors.toList());

            //批量新增到ES中
            searchRepository.saveAll(goodsList);

            page++;
            totalPage = pageResult.getTotalPage();
        }while (page<=totalPage);

    }

    /**
     * 把一个SpuDTO对象转换为一个Goods对象
     */
    public Goods buildGoods(SpuDTO spuDTO){
        Goods goods = new Goods();

        goods.setId(spuDTO.getId());
        goods.setSpuName(spuDTO.getName());
        goods.setSubTitle(spuDTO.getSubTitle());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCreateTime(new Date().getTime());

        //一、处理skus，all，price
        List<Sku> skuList = itemClient.findSkusBySpuId(spuDTO.getId());
        //封装skus属性
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        skuList.forEach(sku -> {
            Map<String,Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("images",sku.getImages());
            skuMap.put("price",sku.getPrice());

            skuMapList.add(skuMap);
        });

        //把List转换Json字符串
        String skuJson = JsonUtils.toString(skuMapList);

        String all = spuDTO.getName()+" "+spuDTO.getSubTitle()+" "+skuList.stream().map(Sku::getTitle).collect(Collectors.joining(" "));

        List<Long> price = skuList.stream().map(Sku::getPrice).collect(Collectors.toList());

        goods.setSkus(skuJson);
        goods.setAll(all);
        goods.setPrice(price);


        //二、处理specs属性
        /**
         * Map<String,Object>
         *     key: 规格参数的名称（查询规格参数tb_spec_param表）
         *     value: 规格参数的数据（查询tb_spu_detail表）
         *
         *  1） 根据分类ID查询用于搜索过滤（searching=true）的规格参数（key有了）
         *  2） 根据spuId查询SpuDetail
         *  3） 取出SpuDetail的genericSpec和specialSpec
         *  4） 取value值
         *      如果generic如果为true，把genericSpec转换为Map集合，根据规格参数ID到Map集合取出value
         *      如果generic如果为false，把specialSpec转换为Map集合，根据规格参数ID到Map集合取出value
         */
        //用于存储最后所有规格参数数据
        Map<String,Object> specs = new HashMap<>();

        //1） 根据分类ID查询用于搜索过滤（searching=true）的规格参数（key有了）
        List<SpecParam> specParams = itemClient.findSpecParams(null, spuDTO.getCid3(), true);
        //2） 根据spuId查询SpuDetail
        SpuDetail spuDetail = itemClient.findSpuDetailBySpuId(spuDTO.getId());
        //3） 取出SpuDetail的genericSpec和specialSpec
        String genericSpec = spuDetail.getGenericSpec();
        String specialSpec = spuDetail.getSpecialSpec();
        //4） 取value值
        //  如果generic如果为true，把genericSpec转换为Map集合，根据规格参数ID到Map集合取出value
        //  如果generic如果为false，把specialSpec转换为Map集合，根据规格参数ID到Map集合取出value
        specParams.forEach(specParam -> {
            String key = specParam.getName();

            Object value = null;

            if(specParam.getGeneric()){
                //通用参数
                //把genericSpec转换为Map集合
                //toMap(): 简单的json字符串
                Map<Long, Object> genericSpecMap = JsonUtils.toMap(genericSpec, Long.class, Object.class);
                //根据规格参数ID到Map集合取出value
                value = genericSpecMap.get(specParam.getId());
            }else{
                //特有参数
                //nativeRead(): 可以转换复杂的json字符串（集合里面还包含集合）
                Map<Long, List<Object>> specialSpecMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<Object>>>() {});
                //根据规格参数ID到Map集合取出value
                value = specialSpecMap.get(specParam.getId());
            }

            //对数字类型的参数，把值转换为区间格式
            if(specParam.getNumeric()){
                value = chooseSegment(value,specParam);
            }


            specs.put(key,value);
        });

        goods.setSpecs(specs);

        return goods;
    }


    /**
     * 把数字参数转换为区间
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(Object value, SpecParam p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public SearchResult<GoodsDTO> goodsPageQuery(SearchRequest searchRequest) {
        //1.创建SearchResult<GoodsDTO>对象
        SearchResult<GoodsDTO> searchResult = new SearchResult<GoodsDTO>();

        //2.封装SearchResult<GoodsDTO>对象
        //itemQueryPage(): 查询商品分页结果
        PageResult<GoodsDTO> pageResult = itemQueryPage(searchRequest);

        //filterConditionsQuery()
        Map<String,Object> filterConditions = filterConditionsQuery(searchRequest);

        searchResult.setItems(pageResult.getItems());
        searchResult.setTotal(pageResult.getTotal());
        searchResult.setTotalPage(pageResult.getTotalPage());
        searchResult.setFilterConditions(filterConditions);

        //3.返回SearchResult<GoodsDTO>对象
        return searchResult;
    }

    /**
     * 查询搜索过滤条件数据
     * @param searchRequest
     * @return
     */
    public Map<String, Object> filterConditionsQuery(SearchRequest searchRequest) {
        //1.创建Map对象
        //LinkedHashMap: 定义有序的Map集合
        Map<String, Object> filterConditions = new LinkedHashMap<>();

        //2.封装Map对象

        //1)构建基本的查询条件
        NativeSearchQueryBuilder queryBuilder = createNativeQueryBuilder(searchRequest);

        //2）添加结果过滤条件
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""},null));

        //3）添加聚合条件
        String categoryAgg = "categoryAgg";
        String brandAgg = "brandAgg";

        //注意：所有Aggration条件都是AggrationBuilders构建来的
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAgg).field("categoryId"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAgg).field("brandId"));

        //4）执行聚合查询
        //AggregatedPage是Page的子接口：Page只能封装分页结果，AggregatedPage既包含分页结果，又包含聚合结果
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(queryBuilder.build(),Goods.class);

        //5)获取所有聚合结果
        Aggregations aggregations = aggregatedPage.getAggregations();

        //7）先取出分类聚合结果
        Terms categoryTerms = aggregations.get(categoryAgg);
        List<Long> categoryIds =  categoryTerms.getBuckets()
                .stream()
                .map(Terms.Bucket::getKeyAsNumber)//把key取出转换Number类型
                .map(Number::longValue) // 把上一步的Number类型转换为Long类型
                .collect(Collectors.toList());

        List<Category> categoryList =  itemClient.findCategoriesByIds(categoryIds);

        //8）取出品牌聚合结果
        Terms brandTerms = aggregations.get(brandAgg);
        List<Long> brandIds =  brandTerms.getBuckets()
                .stream()
                .map(Terms.Bucket::getKeyAsNumber)//把key取出转换Number类型
                .map(Number::longValue) // 把上一步的Number类型转换为Long类型
                .collect(Collectors.toList());

        List<Brand> brandList =  itemClient.findBrandsByIds(brandIds);

        filterConditions.put("分类",categoryList);
        filterConditions.put("品牌",brandList);


        //3.返回Map对象
        return filterConditions;
    }

    /**
     * 构建基本的查询条件
     * @param searchRequest
     * @return
     */
    public NativeSearchQueryBuilder createNativeQueryBuilder(SearchRequest searchRequest) {
        //1.创建本地查询构造器对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //2.设置条件(*)
        //2.1 添加Query条件
        //注意：Query条件都是QueryBuilders构造来的
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //往bool条件中添加must条件
        //boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchRequest.getKey(),"spuName","all"));

        //往bool条件添加过滤条件
        //1)接收页面过滤条件
        Map<String, Object> filterParams = searchRequest.getFilterParams();
        if(filterParams!=null){
            filterParams.entrySet().forEach(entry->{
                String key = entry.getKey();
                Object value = entry.getValue();

                //处理key
                if(key.equals("分类")){
                    key = "categoryId";
                }else if(key.equals("品牌")){
                    key = "brandId";
                }else{
                    key = "specs."+key+".keyword";
                }

                //添加filter条件
                boolQueryBuilder.filter(QueryBuilders.termQuery(key,value));
            });
        }


        queryBuilder.withQuery(boolQueryBuilder);
        return queryBuilder;
    }

    /**
     * 查询商品分页结果
     * @param searchRequest
     * @return
     */
    public PageResult<GoodsDTO> itemQueryPage(SearchRequest searchRequest) {
        //1.创建本地查询构造器对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //2.设置条件(*)
        //2.1 添加Query条件
        //注意：Query条件都是QueryBuilders构造来的
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //往bool条件中添加must条件
        //boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchRequest.getKey(),"spuName","all"));
        queryBuilder.withQuery(boolQueryBuilder);

        //2.2 添加结果过滤条件
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","spuName","subTitle","skus"},null));

        //2.3 添加分页条件
        //注意：PageRequest.of()里面的第一个参数，从0开始计算页码的
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));

        //2.4 添加高亮条件
        HighlightUtils.highlightField(queryBuilder,"spuName");

        //3.执行查询，获取结果
        /**
         * 参数一：使用构造器构造一个查询对象
         * 参数二：指定需要封装数据的对象(需要指定映射过的对象（有@Docuemnt注解）)
         */
        Page<Goods> pageBean = esTemplate.queryForPage(queryBuilder.build(),Goods.class,HighlightUtils.highlightBody(Goods.class,"spuName"));

        //4.处理结果并返回
        //4.1 取出Goods集合
        List<Goods> goodsList = pageBean.getContent();
        //4.2 把Goods的数据拷贝到GoodsDTO对象
        List<GoodsDTO> goodsDTOList = BeanHelper.copyWithCollection(goodsList, GoodsDTO.class);
        //4.3 封装PageResult对象
        PageResult<GoodsDTO> pageResult = new PageResult<GoodsDTO>(
                pageBean.getTotalElements(),
                Long.valueOf(pageBean.getTotalPages()),
                goodsDTOList);
        return pageResult;
    }

    public List<GoodsDTO> goodsChange(SearchRequest searchRequest) {
        //分页查询商品
        PageResult<GoodsDTO> pageResult = itemQueryPage(searchRequest);
        return pageResult.getItems();
    }

    public void createIndex(Long spuId) {
        SpuDTO spuDTO = itemClient.findSpuDTOById(spuId);
        Goods goods = buildGoods(spuDTO);
        searchRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        searchRepository.deleteById(spuId);
    }
}
