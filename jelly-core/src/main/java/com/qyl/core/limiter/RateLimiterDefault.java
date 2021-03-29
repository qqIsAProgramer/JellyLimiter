package com.qyl.core.limiter;

import com.qyl.common.entity.LimiterRule;
import com.qyl.common.enums.LimiterModel;
import com.qyl.core.config.LimiterConfig;
import com.qyl.core.monitor.MonitorClientImpl;
import com.qyl.monitor.client.MonitorClient;
import com.qyl.monitor.entity.Monitor;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * @Author: qyl
 * @Date: 2021/3/29 10:01
 */
public class RateLimiterDefault implements RateLimiter {
    /**
     * 令牌桶
     */
    private AtomicLong bucket = new AtomicLong(0);
    /**
     * 核心配置
     */
    private LimiterRule rule;
    private LimiterConfig config;

    private ScheduledFuture<?> scheduledFuture;

    private MonitorClient monitorClient = new MonitorClientImpl();

    public RateLimiterDefault(LimiterRule rule, LimiterConfig config) {
        this.config = config;
        init(rule);
    }

    @Override
    public MonitorClient getMonitorClient() {
        return this.monitorClient;
    }

    @Override
    public void init(LimiterRule rule) {
        this.rule = rule;
        // 初始化默认使用单体式限流
        putMonolithicToken();
    }

    @Override
    public boolean tryAcquire() {
        if (!rule.isEnable()) {
            // 未开启限流
            return false;
        }
        return tryAcquireMonitor();
    }

    @Override
    public boolean tryAcquire(String user) {
        boolean allow;
        switch (rule.getRuleAuthority()) {
            case AUTHORITY_WHILE:
                allow = rule.getLimitUserList().contains(user);
                break;
            case AUTHORITY_BLACK:
                allow = Stream.of(rule.getLimitUserList()).noneMatch(o -> o.equals(user));
                break;
            default:
                allow = true;
        }
        return allow && tryAcquire();
    }

    /**
     * 限流器监控
     */
    private boolean tryAcquireMonitor() {
        if (rule.getLimiterModel() == LimiterModel.MONOLITHIC) {
            // 本地限流不支持监控
            return tryAcquirePut();
        }
        // 对分布式限流器进行监控
        Monitor monitor = new Monitor();
        monitor.setLocalDateTime(LocalDateTime.now());
        monitor.setApp(rule.getApp());
        monitor.setId(rule.getId());
        monitor.setName(rule.getName());
        monitor.setMonitorTime(rule.getMonitorTime());
        config.getScheduledExecutorService().execute(() -> {
            monitorClient.save(monitor);
        });
        return tryAcquirePut();
    }

    private boolean tryAcquirePut() {
        boolean result = tryAcquireFact();
        // 以分布式的方式检查剩余的令牌数
        putDistributedToken();
        return result;
    }

    /**
     * 判断控制行为
     */
    private boolean tryAcquireFact() {
        if (rule.getTokenRate() == 0) {
            return false;
        }
        boolean result;
        switch (rule.getAcquireModel()) {
            case FAIL_FAST:
                result = tryAcquireFailed();
                break;
            case BLOCKING:
                result = tryAcquireSucceed();
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    /**
     * CAS 获取令牌，没有令牌立即失效
     */
    private boolean tryAcquireFailed() {
        long token = bucket.longValue();
        while (token >= 1) {
            if (bucket.compareAndSet(token, token - 1)) {
                return true;
            }
            token = bucket.longValue();
        }
        return false;
    }

    /**
     * CAS 获取令牌，阻塞直至成功
     */
    private boolean tryAcquireSucceed() {
        long token = bucket.longValue();
        while (!(token >= 1 && bucket.compareAndSet(token, token - 1))) {
            sleep();
            token = bucket.longValue();
        }
        return true;
    }

    /**
     * 线程休眠
     */
    private void sleep() {
        if (rule.getUnit().toMillis(rule.getPeriod()) < 1) {
            return;
        }
        // 大于 1ms 强制休眠
        try {
            Thread.sleep(rule.getUnit().toMillis(rule.getPeriod()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 放置单体式令牌
     */
    private void putMonolithicToken() {
        if (scheduledFuture != null) {
            // 任务不为空
            scheduledFuture.cancel(true);
        }
        if (rule.getTokenRate() == 0 || rule.getLimiterModel() != LimiterModel.MONOLITHIC) {
            return;
        }
        this.scheduledFuture = config.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            if (bucket.get() < rule.getCapacity()) {
                long lost = rule.getCapacity() - bucket.get();
                bucket.getAndAdd(Math.min(lost, rule.getTokenRate()));
            }
        }, 0, rule.getPeriod(), rule.getUnit());
    }

    /**
     * 放置分布式令牌
     */
    private void putDistributedToken() {
        if (rule.getLimiterModel() != LimiterModel.DISTRIBUTE || bucket.get() >= rule.getCapacity()) {
            return;
        }
        // 异步任务
        config.getScheduledExecutorService().execute(() -> {
            // DCL 再次检验
            if (bucket.get() < rule.getCapacity()) {
                synchronized (this) {
                    if (bucket.get() < rule.getCapacity()) {
                        // TODO
                        long lost = rule.getCapacity() - bucket.get();
                        bucket.getAndAdd(Math.min(lost, rule.getTokenRate()));
                    }
                }
            }
        });
    }

    @Override
    public String getId() {
        return this.rule.getId();
    }

    @Override
    public LimiterRule getLimiterRule() {
        return this.rule;
    }
}
