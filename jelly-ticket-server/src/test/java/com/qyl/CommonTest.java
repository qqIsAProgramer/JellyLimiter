package com.qyl;

import com.alibaba.fastjson.JSON;
import com.qyl.common.entity.LimiterRule;
import com.qyl.common.entity.LimiterRuleBuilder;
import com.qyl.common.enums.LimiterModel;
import com.qyl.core.config.LimiterConfig;
import com.qyl.core.limiter.RateLimiter;
import com.qyl.core.limiter.RateLimiterFactory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: qyl
 * @Date: 2021/3/30 14:16
 */
public class CommonTest {

    @Test
    public void test1() throws InterruptedException {
        // 1. 限流配置
        LimiterRule limiterRule = new LimiterRuleBuilder(15)
                .setApp("demo")
                .setId("jelly")
                .setTokenRate(3)  // 每秒 3 个令牌
                .setLimiterModel(LimiterModel.DISTRIBUTE)  // 分布式限流，需启动 TicketServer 控制台
                .build();

        // 2. 配置 TicketServer 地址（支持集群、加权重）
        Map<String, Integer> ticketServer = new HashMap<>();
        ticketServer.put("127.0.0.1:6060", 1);
        // 3. 全局配置
        LimiterConfig config = LimiterConfig.getInstance();
        config.setTicketServer(ticketServer);
        // 4.工厂模式生产限流器
        RateLimiter limiter = RateLimiterFactory.of(limiterRule, config);
        // 5.使用
        while (true) {
            if (limiter.tryAcquire()) {
                System.out.println("ok");
            }

            Thread.sleep(10);
        }
    }
}
