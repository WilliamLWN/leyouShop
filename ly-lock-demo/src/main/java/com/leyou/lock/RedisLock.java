package com.leyou.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式接口
 */
public interface RedisLock {

    //获取锁
    public boolean lock(Integer timeout, TimeUnit unit);
    //释放锁
    public void unlock();

}
