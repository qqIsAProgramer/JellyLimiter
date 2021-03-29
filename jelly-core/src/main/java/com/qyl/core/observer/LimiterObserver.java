package com.qyl.core.observer;

import com.qyl.common.enums.LimiterModel;
import com.qyl.core.config.LimiterConfig;
import com.qyl.core.exception.JellyLimiterException;
import com.qyl.core.limiter.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: qyl
 * @Date: 2021/3/29 14:32
 * 观察者模式
 */
public class LimiterObserver {

    private static Map<String, RateLimiter> limiterMap = new ConcurrentHashMap<>();

    private static Logger logger = LoggerFactory.getLogger(LimiterObserver.class);

    public static Map<String, RateLimiter> getLimiterMap() {
        return limiterMap;
    }

    /**
     * 注册限流器
     * @param limiter 限流器
     * @param config 配置
     */
    public static void register(RateLimiter limiter, LimiterConfig config) {
        if (limiterMap.containsKey(limiter.getId())) {
            throw new JellyLimiterException("Repeat registration for current limiting rules: " + limiter.getId());
        }
        limiterMap.put(limiter.getId(), limiter);  // 放入限流器 Map
        if (limiter.getLimiterRule().getLimiterModel() == LimiterModel.MONOLITHIC) {
            // 本地限流只注册
            return;
        }
//        update(limiter, config);
//        monitor(limiter, config);
    }
}
