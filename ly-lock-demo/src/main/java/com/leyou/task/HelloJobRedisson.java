package com.leyou.task;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 模拟一个业务，该业务需要使用分布式
 */
@Slf4j
@Component
public class HelloJobRedisson {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(cron = "0/5 * * * * ?")
    public void hello() throws InterruptedException {
        //1.创建锁对象
        RLock rLock = redissonClient.getLock("10021011");
        //2.获取锁
        boolean isLock = rLock.tryLock(50, TimeUnit.SECONDS);

        // 判断是否获取锁
        if (!isLock) {
            // 获取失败
            log.info("获取锁失败，停止定时任务");
            return;
        }
        try {
            // 执行业务
            log.info("获取锁成功，执行定时任务。");

            // 模拟任务耗时
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            log.error("任务执行异常", e);
        } finally {
            // 释放锁
            //lock.releaseLock();
            rLock.unlock();
        }
    }
}
