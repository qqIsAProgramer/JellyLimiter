package com.qyl.server.zk;

import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: qyl
 * @Date: 2021/4/10 15:38
 */
public class ZKLock extends ZKAbstractLock {

    private CuratorFramework zkClient = createWithOptions(10 * 1000, 6 * 1000);

    private static final String ROOT_PATH = "/root_lock";
    private CountDownLatch countDownLatch; // 未获得锁时阻塞等待工具
    private String beforePath;  // 当前请求节点的前一个节点
    private String currentPath;  // 当前请求节点

    public ZKLock() {
    }

    @Override
    protected boolean tryLock() {
        return false;
    }

    @Override
    protected void waitLock() {

    }

    @Override
    public void unlock() {

    }
}
