package com.qyl.monitor.client;

import com.qyl.monitor.entity.Monitor;

/**
 * @Author: qyl
 * @Date: 2021/3/28 22:17
 */
public interface MonitorClient {

    /**
     * 保存一条监控数据
     * @param monitor
     */
    void save(Monitor monitor);
}
