package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 分布式锁微服务
 */
@SpringBootApplication
@EnableScheduling // 开启定时任务
public class LyLockApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyLockApplication.class, args);
    }
}
