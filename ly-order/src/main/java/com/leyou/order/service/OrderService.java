package com.leyou.order.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.item.pojo.Sku;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.dto.OrderVO;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderLogisticsMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderLogistics;
import com.leyou.order.pojo.OrderStatusEnum;
import com.leyou.order.utils.PayHelper;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private PayHelper payHelper;

    @GlobalTransactional
    public Long buildOrder(OrderDTO orderDTO) {
        UserInfo userInfo = UserHolder.getUser();


        //1.保存订单表数据
        Order order = new Order();
        order.setOrderId(idWorker.nextId());//利用雪花算法生成分布式ID

        //计算订单总金额
        //1)取出所有CartDTO
        List<CartDTO> cartDTOList = orderDTO.getCarts();
        //2）取出所有SkuId
        List<Long> skuIdList = cartDTOList.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        //3）根据SkuId集合查询Sku对象集合
        List<Sku> skuList = itemClient.findSkusByIds(skuIdList);
        //4）设计一个Map集合，存储所有skuId和num
        Map<Long,Integer> skuMap = cartDTOList.stream().collect(Collectors.toMap( CartDTO::getSkuId , CartDTO::getNum ));
        //5）计算商品总价
        Long totalFee = skuList.stream().mapToLong(sku->sku.getPrice()*skuMap.get(sku.getId())).sum();

        order.setTotalFee(totalFee);//总价
        order.setActualFee(1L);//实付金额
        order.setPromotionIds("1");//优惠活动
        order.setPaymentType(orderDTO.getPaymentType());//支付类型
        order.setPostFee(50L);//邮费
        order.setUserId(userInfo.getId());//下单人的ID
        order.setInvoiceType(0);//发票类型
        order.setSourceType(2);//订单来源
        order.setStatus(OrderStatusEnum.INIT.value());//订单状态
        order.setCreateTime(new Date(System.currentTimeMillis()));
        order.setUpdateTime(new Date(System.currentTimeMillis()));

        orderMapper.insert(order);


        //2.保存订单明细表数据
        if(CollectionUtils.isNotEmpty(skuList)){
            skuList.forEach(sku->{
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setId(idWorker.nextId());//明细ID
                orderDetail.setSkuId(sku.getId());
                orderDetail.setOrderId(order.getOrderId());//订单ID
                orderDetail.setNum(skuMap.get(sku.getId()));//购买数量
                orderDetail.setTitle(sku.getTitle());//标题
                orderDetail.setOwnSpec(sku.getOwnSpec());//特有参数
                orderDetail.setPrice(sku.getPrice());//价格
                orderDetail.setImage(sku.getImages());//图片
                orderDetail.setCreateTime(new Date(System.currentTimeMillis()));
                orderDetail.setUpdateTime(new Date(System.currentTimeMillis()));

                orderDetailMapper.insert(orderDetail);
            });
        }


        //3.保存订单的物流信息表数据
        //1）根据地址ID查询收货地址信息
        AddressDTO addressDTO = userClient.findAddressById(userInfo.getId(), orderDTO.getAddressId());
        //2）把AddressDTO数据拷贝到物流对象中
        OrderLogistics orderLogistics = BeanHelper.copyProperties(addressDTO, OrderLogistics.class);
        //3）补充信息
        orderLogistics.setOrderId(order.getOrderId());//订单ID
        orderLogistics.setLogisticsNumber("SF0001");//物流单号
        orderLogistics.setLogisticsCompany("顺丰物流");//物流公司名称
        orderLogistics.setCreateTime(new Date(System.currentTimeMillis()));
        orderLogistics.setUpdateTime(new Date(System.currentTimeMillis()));
        //4）保存物流信息
        orderLogisticsMapper.insert(orderLogistics);

        //4.扣减商品相应库存
        itemClient.minusStock(skuMap);

        //测试Seata事务回滚,看订单创建失败时商品库存扣减是否回滚
//        int i = 1/0;

        //返回订单ID
        return order.getOrderId();
    }

    public OrderVO findOrderById(Long id) {
        //1.查询Order
        Order order = orderMapper.selectById(id);
        //拷贝数据
        OrderVO orderVO = BeanHelper.copyProperties(order, OrderVO.class);
        //2.查询物流信息，并封装
        OrderLogistics orderLogistics = orderLogisticsMapper.selectById(id);
        orderVO.setLogistics(orderLogistics);
        //3.查询订单明细
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        QueryWrapper<OrderDetail> queryWrapper = Wrappers.query(orderDetail);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper);
        orderVO.setDetailList(orderDetails);
        return orderVO;
    }

    public String getPayUrl(Long id) {
        //先读取redis的支付链接
        String payUrl = redisTemplate.opsForValue().get(LyConstants.PAY_URL_PRE+id);

        //如果redis没有支付链接，到微信支付系统获取新的支付链接，把支付链接存入redis,设置过期时间为2小时
        if(StringUtils.isEmpty(payUrl)){
            //根据订单ID查询订单
            Order order = orderMapper.selectById(id);
            payUrl = payHelper.getPayUrl(id,order.getActualFee());

            redisTemplate.opsForValue().set(LyConstants.PAY_URL_PRE+id,payUrl,2, TimeUnit.HOURS);
        }

        //如果redis存在支付链接，直接使用redis支付链接即可
        return payUrl;
    }

    public void wxNotify(Map<String, String> paramMap) {
        //1.接收参数
        Long orderId = Long.valueOf(paramMap.get("out_trade_no"));
        Long totalFee = Long.valueOf(paramMap.get("total_fee"));

        //2.数据校验
        //查询数据库订单
        Order order = orderMapper.selectById(orderId);
        if(order==null){
            log.error("【支付回调】非法订单");
            throw new LyException(500,"非法订单");
        }

        if(order.getActualFee()!=totalFee){
            log.error("【支付回调】订单实付金额不一致");
            throw new LyException(500,"订单实付金额不一致");
        }

        //2.更新订单信息
        try {
            order.setStatus(OrderStatusEnum.PAY_UP.value());
            order.setPayTime(new Date());
            orderMapper.updateById(order);

            log.info("【支付回调】订单信息更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【支付回调】订单信息更新失败");
            throw new LyException(500,"订单信息更新失败");
        }

    }

    public Integer checkState(Long id) {
        Order order = orderMapper.selectById(id);
        return order.getStatus();
    }
}

