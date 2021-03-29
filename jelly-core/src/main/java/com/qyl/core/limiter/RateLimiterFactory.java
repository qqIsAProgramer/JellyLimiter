package com.qyl.core.limiter;

import com.qyl.common.entity.LimiterRule;
import com.qyl.core.config.LimiterConfig;
import com.qyl.core.exception.JellyLimiterException;
import com.qyl.core.observer.LimiterObserver;

/**
 * @Author: qyl
 * @Date: 2021/3/29 14:30
 * 简单工厂模式
 */
public class RateLimiterFactory {

    /**
     * @param rule 规则
     * @return RateLimiter
     */
    public static RateLimiter of(LimiterRule rule) {
        return of(rule, LimiterConfig.getInstance());
    }

    /**
     * @param rule 规则
     * @param config 配置
     * @return RateLimiter
     */
    public static RateLimiter of(LimiterRule rule, LimiterConfig config) {
        RateLimiter rateLimiter;
        switch (rule.getLimiterModel()) {
            case MONOLITHIC:  // 单体式限流
                rateLimiter = new RateLimiterDefault(rule, config);
                break;
            case DISTRIBUTE:  // 分布式限流
                rateLimiter = new RateLimiterDefault(rule, config);
                rule.setName(rule.getName() == null ? String.valueOf(rateLimiter.hashCode()) : rule.getName());
                break;
            default:
                throw new JellyLimiterException("Current model parameter has not set");
        }
        LimiterObserver.register(rateLimiter, config);  // 注册限流器
        return rateLimiter;
    }
}
