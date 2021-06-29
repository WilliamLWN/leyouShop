package com.leyou.lock;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisLockImpl2 implements RedisLock {

    private StringRedisTemplate redisTemplate;
    private String key;

    private String value = "1";

    public RedisLockImpl2(StringRedisTemplate redisTemplate,String key){
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    @Override
    public boolean lock(Integer timeout, TimeUnit unit) {
        //获取当前线程ID
        String threadId = Thread.currentThread().getId()+"";
        this.value = threadId;
        /**
         * setIfAbsent(): 底层就是发送setnx指令
         *    如果该key不存在，则设置key和value到redis，返回1
         *    如果该key存在，无法设置key和value，返回0
         */
        return redisTemplate.opsForValue().setIfAbsent(key,value,timeout,unit);
    }

    @Override
    public void unlock() {

        //取出redis的value，判断value值是否为当前线程ID
        String dbValue = redisTemplate.opsForValue().get(key);
        String threadId = Thread.currentThread().getId()+"";

        //判断redis存储的线程ID等于当前线程ID，才可以删除锁记录
        if(dbValue.equals(threadId)){
            redisTemplate.delete(key);
        }
    }
}
