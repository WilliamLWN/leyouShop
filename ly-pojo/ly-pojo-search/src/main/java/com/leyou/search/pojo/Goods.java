package com.leyou.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品实体，与ES的文档映射
 * 一个Spu对象对应一个Goods对象
 * /**
 *  * 商品索引库对象
 *  *  @Document: 映射ES的文档
 *  *     indexName: 索引库
 *  *     type: 类型
 *  *     shards: 分片数量
 *  *     replicas：副本数量
 *  *  @Id: 文档主键映射
 *  *  @Field:
 *  *     type: 该字段的类型
 *  *        Text： 字符串类型。分词的类型
 *  *        Keyword： 字符串类型。不分词的类型
 *  *        Integer/Long/Float/Double: 数值类型。不分词的类型
 *  *        Date： 日期类型。不分词的类型
 *  *        Boolean： 布尔类型。不分词的类型
 *  *        Object: 对象类型，包含自定义对象，List，Set, Map。在ES中对象类型，里面的每个属性都是索引和分词的。
 *  *     index：该字段是否索引（如果该字段参与搜索，该字段就必须索引）
 *  *     analyzer: 指定分词器 （如果该索引字段需要被分词后搜索，该字段通常指定分词器）
 *  *          常用的ik分词器
 *  *              ik_smart: 最小分词。 我是程序员 -> 我  是  程序员
 *  *              ik_max_word：最细分词。  我是程序员 -> 我  是  程序员 程序 员
 *  *     store: 该字符串是否存储。（如果该字段在结果中需要被显示出来，该字段就要存储） 默认true
 *  *  注意：一个Goods对象对应一条Spu的记录
 *  */
@Data
@Document(indexName = "goods",type = "docs",shards = 1,replicas = 1)
public class Goods {
    @Id
    private Long id;//存储spuId
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String spuName;//spu表的name属性，因为该字段参与高亮显示，所以必须索引，必须分词
    @Field(type = FieldType.Keyword,index = false)
    private String subTitle;//副标题，不索引
    @Field(type = FieldType.Keyword,index = false)
    private String skus;//Spu下的所有Sku数据，由List集合转换成json字符串

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all;//用于匹配搜索关键词的字段：三个分类名称+品牌名称+spuName+subTitle+所有Sku的title

    private Long categoryId;//分类ID，固定过滤条件，通过categoryId聚合而来
    private Long brandId;//品牌ID，固定过滤条件，通过brandId聚合而来

    /**
     * 动态过滤条件格式：
     *   1）通用参数格式： {"品牌":"华为"}
     *   2）特有参数格式： {"机身颜色":["白色","黑色",...]}
     */
    @Field(type = FieldType.Object)
    private Map<String,Object> specs;//动态过滤条件

    @Field(type = FieldType.Long)
    private Long createTime;//创建时间
    private List<Long> price;//价格

}
