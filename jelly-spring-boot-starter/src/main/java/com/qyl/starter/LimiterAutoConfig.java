package com.qyl.starter;

import com.qyl.starter.aspect.LimiterAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: qyl
 * @Date: 2021/3/23 22:51
 * 自动配置
 */
@ComponentScan
@Configuration
public class LimiterAutoConfig {

    /**
     * 注入 LimiterAspect
     */
    @Bean
    public LimiterAspect rateLimiterAspect() {
        return new LimiterAspect();
    }
}
