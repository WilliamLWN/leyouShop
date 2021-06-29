package com.leyou;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.sharding.mapper.OrderMapper;
import com.leyou.sharding.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereApplication.class)
public class ShardingTest {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 批量增加
     */
    @Test
    public void testInsert(){
        Order order = null;
        Random random = new Random();
        for(int i=1;i<=100;i++){
            order = new Order();
            order.setUserId(1L);// 使用工具类获取拦截器传递过来的用户id
            order.setStatus(1); // 订单状态
            order.setSourceType(2); // 订单来源 1:app端，2：pc端，3：微信端
            order.setPostFee(0L);// 邮费：全场包邮
            order.setPaymentType(1);// 支付类型：在线支付
            order.setInvoiceType(0);// 发票类型，0无发票，1普通发票，2电子发票，3增值税发票
            order.setActualFee(1L);//实际支付 = 总金额 - 活动金额 ； 这里为了测试我们写个1分
            order.setTotalFee(Long.valueOf(random.nextInt(1000)+i)); // 总金额

            orderMapper.insert(order);
        }
    }

    /**
     * 查询所有
     *   问题1： 查询所有记录，执行几条SQL语句？ 6条（表数量）
     *   问题2： 分库分表后的查询所有数据会比没有分库分表快么？
     *             当一张表的记录少的时候，分库分表反而慢
     *             当一张表的记录多的时候，分库分表会快
     */
    @Test
    public void testFindAll(){
        List<Order> orders = orderMapper.selectList(null);
        System.out.println("总记录数："+orders.size());
    }

    /**
     * 根据id查询
     *   问题：执行了几条sql？ 1条
     *
     */
    @Test
    public void testFindById(){
        Order order = orderMapper.selectById(1405743373611962369L);
        System.out.println("记录："+order);
    }

    /**
     * 根据其他字段查询
     *   问题：执行了几条sql？ 6条
     *
     */
    @Test
    public void testFindByStatus(){
        Order order = new Order();
        order.setStatus(2);
        QueryWrapper<Order> queryWrapper = Wrappers.query(order);

        List<Order> list = orderMapper.selectList(queryWrapper);
        System.out.println("记录数："+list.size());
    }
}

