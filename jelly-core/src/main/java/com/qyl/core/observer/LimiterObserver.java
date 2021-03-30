package com.qyl.core.observer;

import com.alibaba.fastjson.JSON;
import com.qyl.common.entity.LimiterRule;
import com.qyl.common.enums.LimiterModel;
import com.qyl.common.utils.ServerAddress;
import com.qyl.core.config.LimiterConfig;
import com.qyl.core.exception.JellyLimiterException;
import com.qyl.core.limiter.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
        update(limiter, config);
    }

    /**
     * 发送心跳并更新限流规则
     * @param limiter 限流器
     * @param config 配置
     */
    private static void update(RateLimiter limiter, LimiterConfig config) {
        config.getScheduledExecutorService().scheduleWithFixedDelay(() -> {
            String data = config.getTicketServer().connect(ServerAddress.HEARTBEAT_PATH, JSON.toJSONString(limiter.getLimiterRule()));
            // 自动故障服务检测与切换
            if (data == null) {  // TicketServer 挂掉
                // 转换为单机
                logger.debug("Update limiter fail, automatically switch to local current limiter");
                LimiterRule rule = limiter.getLimiterRule();
                rule.setLimiterModel(LimiterModel.MONOLITHIC);
                limiter.init(rule);
                return;
            }

            LimiterRule limiterRule = JSON.parseObject(data, LimiterRule.class);
            if (limiterRule.getVersion() > limiter.getLimiterRule().getVersion()) {  // 版本升级
                logger.info("Update rule version: {} -> {}", limiter.getLimiterRule().getVersion(), limiterRule.getVersion());
                limiterMap.get(limiter.getId()).init(limiterRule);
            } else if (limiterRule.getLimiterModel() == LimiterModel.MONOLITHIC) {  // 服务切换
                // 为什么单体式服务会走到这一步呢？
                // 因为当该限流器初始是分布式限流器，但由于 TicketServer 挂掉，转变为单体限流
                // 但如果 TicketServer 负载均衡到其他的服务地址，那么会重新拿到数据，进而可以转变为分布式
                limiterRule.setLimiterModel(LimiterModel.DISTRIBUTE);
                limiterMap.get(limiter.getId()).init(limiterRule);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
