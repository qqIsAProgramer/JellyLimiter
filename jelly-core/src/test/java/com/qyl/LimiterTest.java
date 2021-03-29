package com.qyl;

import com.qyl.common.entity.LimiterRule;
import com.qyl.common.entity.LimiterRuleBuilder;
import com.qyl.core.limiter.RateLimiter;
import com.qyl.core.limiter.RateLimiterFactory;
import org.junit.Test;

/**
 * @Author: qyl
 * @Date: 2021/3/29 14:37
 */
public class LimiterTest {

    @Test
    public void test1() throws InterruptedException {
        LimiterRule limiterRule = new LimiterRuleBuilder(10)
                .setTokenRate(2)
                .setPeriod(1)
                .build();
        RateLimiter rateLimiter = RateLimiterFactory.of(limiterRule);

        Thread.sleep(7000);
        while (true) {
            if (rateLimiter.tryAcquire()) {
                System.out.println("ok");
            }
        }
    }
}
