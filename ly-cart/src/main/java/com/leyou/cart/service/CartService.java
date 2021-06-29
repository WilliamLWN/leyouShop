package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;
import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void addCart(Cart cart) {
        //1.查询当前登录用户的购物车数据
        BoundHashOperations<String, Object, Object> boundHashOps = getLoginUserCarts();

        //2.判断当前商品是否在购物车中
        String skuId = cart.getSkuId().toString();

        if(boundHashOps.hasKey(skuId)){
            //2.1 如果在，则取出指定商品，修改其数量
            //1）取出指定商品json字符串
            String json = (String)boundHashOps.get(skuId);
            //2）把json字符串转换Cart对象
            Cart oldCart = JsonUtils.toBean(json, Cart.class);
            //3）修改数量
            cart.setNum( oldCart.getNum()+cart.getNum() );
        }
        //2.2 如果不在，则添加该商品到购物车中
        boundHashOps.put(skuId,JsonUtils.toString(cart));

    }

    public List<Cart> loadCarts() {
        //1.获取登录用户的购物车数据
        BoundHashOperations<String, Object, Object> boundHashOps = getLoginUserCarts();
        //2.返回购物车数据
        /**
         * boundHashOps的values方法： 取出Map结构的value值的集合
         */
        return boundHashOps.values()
                .stream()
                .map( json->JsonUtils.toBean((String)json,Cart.class) )
                .collect(Collectors.toList());
    }

    /**
     * 获取登录用户的购物车数据
     */
    public BoundHashOperations<String, Object, Object> getLoginUserCarts() {
        //1）从ThreadLo取出UserInfo
        UserInfo userInfo = UserHolder.getUser();
        String userId = userInfo.getId().toString();

        //2）到redis中取出我的购物车,redis用hash数据类型
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(LyConstants.CART_PRE + userId);

        //3）返回取出的购物车数据
        return boundHashOps;
    }

    public void updateNum(Long skuId, Integer num) {
        //1.redis获取登录用户的购物车数据
        BoundHashOperations<String, Object, Object> boundHashOps = getLoginUserCarts();
        //2.在购物车取出修改数量的商品
        String skuIdStr = skuId.toString();
        String json = (String)boundHashOps.get(skuIdStr);
        //3.转换为Cart对象
        Cart cart = JsonUtils.toBean(json, Cart.class);
        //4.修改数量
        cart.setNum(num);
        //5.覆盖回原来的商品数据
        boundHashOps.put(skuIdStr,JsonUtils.toString(cart));
    }

    public void deleteCart(Long skuId) {
        //1.获取登录用户的购物车数据
        BoundHashOperations<String, Object, Object> boundHashOps = getLoginUserCarts();
        //2.删除购物车中指定商品
        boundHashOps.delete(skuId.toString());
    }

    public void mergeCarts(List<Cart> carts) {
        //批量新增，其实就是循环把集合中的每个购物车商品添加到redis。因此这里可以调用之前 单商品新增的逻辑。
        carts.forEach(cart -> {
            addCart(cart);
        });
    }

}
