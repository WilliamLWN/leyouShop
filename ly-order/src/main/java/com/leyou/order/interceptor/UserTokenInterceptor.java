package com.leyou.order.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.leyou.order.config.JwtProperties;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取用户登录信息拦截器
 */
@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProps;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.取出token
        String token = CookieUtils.getCookieValue(request,jwtProps.getCookie().getCookieName());

        if(StringUtils.isEmpty(token)){
            //拒绝访问
            return false;
        }

        //2.校验token合法性
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProps.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            //拒绝访问
            return false;
        }

        //3.取出载荷的UserInfo信息
        UserInfo userInfo = payload.getInfo();

        //4.存入ThreadLocal
        UserHolder.setUser(userInfo);

        //放行
        return true;
    }
}
