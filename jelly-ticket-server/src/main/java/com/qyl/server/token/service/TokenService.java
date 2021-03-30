package com.qyl.server.token.service;

import com.qyl.common.entity.LimiterRule;

/**
 * @Author: qyl
 * @Date: 2021/3/29 20:24
 */
public interface TokenService {

    /**
     * 从 Redis 中获取 token
     */
    Long getToken(LimiterRule limiterRule);
}
