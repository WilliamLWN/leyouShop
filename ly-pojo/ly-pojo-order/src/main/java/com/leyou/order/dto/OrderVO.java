package com.leyou.order.dto;

import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderLogistics;
import lombok.Data;

import java.util.List;


@Data
public class OrderVO extends Order {

    /**
     * 订单物流信息
     */
    private OrderLogistics logistics;
    /**
     * 订单详情信息
     */
    private List<OrderDetail> detailList;
}