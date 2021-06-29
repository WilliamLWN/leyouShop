package com.leyou.order.config;

import com.leyou.order.interceptor.UserTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserTokenInterceptor userTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        /**
         * addPathPatterns(): 添加拦截路径。默认就是拦截全部路径  /**
         * excludePathPatterns(): 添加放行路径。
         */
        registry.addInterceptor(userTokenInterceptor).addPathPatterns("/**").excludePathPatterns("/wx/notify/**"); //对微信的回调地址放开配置
    }
}


