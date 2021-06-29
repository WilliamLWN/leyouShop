package com.leyou.order.controller;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.dto.OrderVO;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/order")
    public ResponseEntity<Long> buildOrder(
            @RequestBody OrderDTO orderDTO
    ){
        Long orderId = orderService.buildOrder(orderDTO);
        return ResponseEntity.ok(orderId);
    }

    /**
     * 根据ID查询订单
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderVO> findOrderById(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.findOrderById(id);
        return ResponseEntity.ok(orderVO);
    }

    /**
     * 获取支付链接
     */
    @GetMapping("/order/url/{id}")
    public ResponseEntity<String> getPayUrl(@PathVariable("id") Long id){
        String payUrl = orderService.getPayUrl(id);
        return ResponseEntity.ok(payUrl);
    }

    /**
     * 支付回调接口
     *  produces: 声明返回值类型
     */
    @PostMapping(value = "/wx/notify",produces = "application/xml")
    public Map<String,String> wxNotify(@RequestBody Map<String,String> paramMap){
        orderService.wxNotify(paramMap);

        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("return_code","SUCCESS");
        resultMap.put("return_msg","OK");
        return resultMap;
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/order/state/{id}")
    public ResponseEntity<Integer> checkState(@PathVariable("id") Long id){
        Integer state = orderService.checkState(id);
        return ResponseEntity.ok(state);
    }
}