package com.qyl.core.config;

import com.qyl.core.exception.JellyLimiterException;
import com.qyl.core.ticket.TicketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: qyl
 * @Date: 2021/3/28 22:40
 */
public class LimiterConfig {

    private static Logger logger = LoggerFactory.getLogger(LimiterConfig.class);
    /**
     * 单例模式
     */
    private static volatile LimiterConfig limiterConfig;

    private TicketServer ticketServer;  // 发票服务器

    private ScheduledExecutorService scheduledExecutorService;  // 线程池调度

    private LimiterConfig() {
        // 禁止实例化
    }

    /**
     * @return 唯一实例（以下皆是）
     */
    public static LimiterConfig getInstance() {
        // DCL(Double Check Lock) 双重检验
        if (limiterConfig == null) {
            synchronized (LimiterConfig.class) {
                if (limiterConfig == null) {
                    logger.info("Starting [jelly limiter]");
                    limiterConfig = new LimiterConfig();
                }
            }
        }
        return limiterConfig;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        if (scheduledExecutorService == null) {
            synchronized (this) {
                if (scheduledExecutorService == null) {
                    // Runtime.getRuntime().availableProcessors(): 获取 CPU 核数
                    // 核心线程数：CPU 核数 * 2
                    scheduledExecutorService = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        return scheduledExecutorService;
    }

    public TicketServer getTicketServer() {
        if (this.ticketServer == null) {
            throw new JellyLimiterException("Ticket server is null");
        }
        return this.ticketServer;
    }

    public void setTicketServer(Map<String, Integer> ip) {
        if (ip.size() < 1) {
            throw new JellyLimiterException("Ip is null");
        }
        if (this.ticketServer == null) {
            synchronized (this) {
                if (this.ticketServer == null) {
                    this.ticketServer = new TicketServer();
                }
            }
        }
        this.ticketServer.setServer(ip);
    }
}
