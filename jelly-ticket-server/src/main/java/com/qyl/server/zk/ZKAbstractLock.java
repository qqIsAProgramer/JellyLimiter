package com.qyl.server.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: qyl
 * @Date: 2021/4/10 15:18
 */
public abstract class ZKAbstractLock implements Lock {

    protected Logger logger = LoggerFactory.getLogger(ZKAbstractLock.class);

    protected final String CONNECT_STRING = "127.0.0.1:2181";
    protected final RetryPolicy retryPolicy = new RetryUntilElapsed(1000, 4);

    protected CuratorFramework createWithOptions(int connectionTimeoutMs, int sessionTimeoutMs) {
        return CuratorFrameworkFactory.builder().connectString(CONNECT_STRING)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }

    public void lock() {
        if (tryLock()) {
            logger.info("获得锁资源");
        } else {
            // 阻塞等待锁
            waitLock();
            // 重新获取锁
            lock();
        }
    }

    /**
     * 占有锁
     */
    protected abstract boolean tryLock();
    /**
     * 等待锁
     */
    protected abstract void waitLock();
}
