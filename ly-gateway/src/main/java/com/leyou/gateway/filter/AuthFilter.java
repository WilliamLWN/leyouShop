package com.leyou.gateway.filter;

import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 用户统一鉴权
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties jwtProps;
    @Autowired
    private FilterProperties filterProps;

    /**
     * 编写过滤逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //取出request和response

        //ServerHttpRequest来自于Spring框架，HttpServletRequest来自于Oracle的，ServerHttpRequest对HttpServletRequest的二次封装。
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();


        //加入拦截白名单逻辑
        //1）取出白名单路径
        List<String> allowPaths = filterProps.getAllowPaths();
        //2）获取到当前访问路径
        String curPath = request.getURI().getPath(); //  /api/item/category/of/parent
        //3）判断当前路径是否在白名单中，在，则放行
        for(String allowPath:allowPaths){
            if(curPath.contains(allowPath)){
                //放行到微服务
                return chain.filter(exchange);
            }
        }


        //1）从请求中取出token（Cookie取出）
        /**
         * getCookies(): 取出所有Cookie
         * getFirst(): 获取指定name的第一个Cookie
         * getValue(): 取出Cookie的值
         */

        String token = null;
        try {
            token = request.getCookies().getFirst(jwtProps.getCookie().getCookieName()).getValue();
        } catch (Exception e) {
            //拒绝访问
            //返回401的状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //中止访问
            return response.setComplete();
        }

        //2）校验token是否合法
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token,jwtProps.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            //拒绝访问
            //返回401的状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //中止访问
            return response.setComplete();
        }


        /*
            3）取出当前用户ID，使用用户ID查询当前用户拥有的权限列表（基于RBAC表结构）
                1    /user  DELETE   删除用户
                2   /user   POST      添加用户
                3   /item/category  POST  分类添加
            4）获取当前访问的URL(/user)和请求方式(POST)
            5）判断当前访问URL和请求方式是否在当前用户拥有的权限列表中，存在，则放行访问微服务，反之，拒绝访问。
        */

        //放行到微服务
        return chain.filter(exchange);
    }

    /**
     返回过滤器执行顺序值
     数值越大，优先级越低

     注意：自定义过滤器的order不要太大
     */
    @Override
    public int getOrder() {
        return 1;
    }
}

