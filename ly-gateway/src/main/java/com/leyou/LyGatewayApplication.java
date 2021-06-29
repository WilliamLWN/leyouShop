package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 网关微服务
 */
/*@SpringBootApplication
@EnableDiscoveryClient // 开启服务注册
@EnableCircuitBreaker // 开启熔断器*/
@SpringCloudApplication
public class LyGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyGatewayApplication.class,args);
    }
}
