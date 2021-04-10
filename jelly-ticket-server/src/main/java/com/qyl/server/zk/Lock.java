package com.qyl.server.zk;

/**
 * @Author: qyl
 * @Date: 2021/4/10 14:50
 */
public interface Lock {
    /**
     * 获取锁资源
     */
    void lock();
    /**
     * 释放锁资源
     */
    void unlock();
}
