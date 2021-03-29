package com.qyl.core.monitor;

import com.qyl.common.utils.DateTimeUtil;
import com.qyl.monitor.client.MonitorClient;
import com.qyl.monitor.entity.Monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: qyl
 * @Date: 2021/3/28 22:46
 */
public class MonitorClientImpl implements MonitorClient {

    private Map<String, Monitor> map = new HashMap<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private void initMap(String dateTime) {
        if (!map.containsKey(dateTime)) {
            lock.writeLock().lock();
            try {
                if (!map.containsKey(dateTime)) {
                    map.put(dateTime, new Monitor(DateTimeUtil.parse(dateTime)));
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void save(Monitor monitor) {
        initMap(monitor.getDateTime());
        lock.readLock().lock();
        try {
            Monitor m = map.get(monitor.getDateTime());
            m.setApp(monitor.getApp());
            m.setId(monitor.getId());
            m.setName(monitor.getName());
            m.setMonitorTime(monitor.getMonitorTime());
        } finally {
            lock.readLock().unlock();
        }
    }
}
