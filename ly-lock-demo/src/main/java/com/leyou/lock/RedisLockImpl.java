package com.leyou.lock;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 实现分布式
 */
public class RedisLockImpl implements RedisLock {
    private StringRedisTemplate redisTemplate;
    private String key;

    private String value = "1";

    public RedisLockImpl(StringRedisTemplate redisTemplate, String key){
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    /**
     * 获取锁
     */
    public boolean lock(Integer timeout, TimeUnit unit){
        //获取当前线程ID
        String threadId = Thread.currentThread().getId()+"";
        this.value = threadId;
        /**
         * setIfAbsent(): 底层就是发送setnx指令
         *    如果该key不存在，则设置key和value到redis，返回1
         *    如果该key存在，无法设置key和value，返回0
         */
        return redisTemplate.opsForValue().setIfAbsent(key,value,timeout,unit);
    };


    /**
     * 释放锁
     */
    public void unlock(){
        redisTemplate.delete(key);
    }

}

