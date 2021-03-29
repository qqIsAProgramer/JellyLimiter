package com.qyl.core.ticket;

import com.qyl.common.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: qyl
 * @Date: 2021/3/22 16:58
 * 发票服务器，实现负载均衡
 * 自动故障服务检测与切换
 */
public class TicketServer {

    private static Logger logger = LoggerFactory.getLogger(TicketServer.class);

    private List<String> serverList = new CopyOnWriteArrayList<>();  // 服务器列表（读多写少）
    private List<String> backupsList = new CopyOnWriteArrayList<>();  // 备份

    private ReentrantLock lock = new ReentrantLock();

    private int pos = 0;  // 负责选择服务器

    private long start = 0;  // 时间戳

    public void setServer(Map<String, Integer> ip) {
        // 清空 list
        serverList.clear();
        // 重建一个 Map，避免服务上下线导致的并发问题
        // key: 服务地址  value: 权重（负载均衡）
        Map<String, Integer> serverMap = new HashMap<>(ip);
        // 取得 ip 地址 list
        for (String server : serverMap.keySet()) {
            int weight = serverMap.get(server);
            for (int i = 0; i < weight; i++) {
                serverList.add(server);
            }
        }
    }

    /**
     * 发送请求
     * @param path 路径
     * @param data 数据（JSON 格式）
     * @return 请求的响应结果
     */
    public String connect(String path, String data) {
        String server = getServer();
        try {
            return HttpUtil.connect("http://" + server + "/" + path)
                    .setData("data", data)
                    .setMethod("POST")
                    .execute()
                    .getBody();
        } catch (IOException e) {
            if (System.currentTimeMillis() - start > 3000) {
                logger.error("{}: the server is not available", server);
                start = System.currentTimeMillis();
            }
            serverList.remove(server);
            backupsList.add(server);
        }
        return null;
    }

    /**
     * 实现负载均衡
     */
    private String getServer() {
        String server;
        lock.lock();
        try {
            if (serverList.size() == 0) {
                serverList.addAll(backupsList);
                backupsList.clear();
            }
            if (pos >= serverList.size()) {
                pos = 0;
            }
            server = serverList.get(pos);
            pos++;
        } finally {
            lock.unlock();
        }
        return server;
    }
}
