package com.qyl.server.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author: qyl
 * @Date: 2021/3/27 11:21
 */
@Configuration
public class ThreadManager {

    @Resource
    private ThreadProperties threadProperties;

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(threadProperties.getSize());
    }
}
