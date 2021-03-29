package com.qyl.server.redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: qyl
 * @Date: 2021/3/26 15:32
 */
@Component
public class SingleRedisLock {

    @Resource
    private RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "redis:lock:";

    /**
     * 获取锁
     */
    public void acquire(String lockName) {
        String key = LOCK_PREFIX + lockName;
        RLock rlock = redissonClient.getLock(key);
        rlock.lock(5, TimeUnit.MINUTES);  // timeout 结束强制解锁，防止死锁
    }

    /**
     * 释放锁
     */
    public void release(String lockName) {
        String key = LOCK_PREFIX + lockName;
        RLock rlock = redissonClient.getLock(key);
        rlock.unlock();
    }
}
