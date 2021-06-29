package com.leyou.task;

import com.leyou.lock.RedisLockImpl;
import com.leyou.lock.RedisLockImpl2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 黑马程序员
 */
@Slf4j
@Component
public class HelloJob {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0/10 * * * * ?")
    public void hello() {
        // 创建锁对象
        RedisLockImpl2 lock = new RedisLockImpl2(redisTemplate,"lock");
        // 获取锁,设置自动失效时间为50s
        boolean isLock = lock.lock(50, TimeUnit.MILLISECONDS);
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
            lock.unlock();
        }
    }
}
