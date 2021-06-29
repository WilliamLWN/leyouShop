package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车
 */
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 加入购物车
     */
    @PostMapping("/")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> loadCarts(){
        List<Cart> carts = cartService.loadCarts();
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购买数量
     */
    @PutMapping("/")
    public ResponseEntity<Void> updateNum(
            @RequestParam("skuId") Long skuId,
            @RequestParam("num") Integer num
    ){
        cartService.updateNum(skuId,num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteCart(
            @RequestParam("skuId") Long skuId
    ){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 合并购物车数据
     */
    @PostMapping("/list")
    public ResponseEntity<Void> mergeCarts( @RequestBody List<Cart> carts){
        cartService.mergeCarts(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

