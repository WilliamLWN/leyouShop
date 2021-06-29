package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 搜索微服务
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // 开启远程Feign调用功能
public class LySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySearchApplication.class,args);
    }
}
