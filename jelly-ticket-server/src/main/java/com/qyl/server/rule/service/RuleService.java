package com.qyl.server.rule.service;

import com.qyl.common.entity.LimiterRule;
import com.qyl.server.utils.ResponseResult;

import java.util.List;

/**
 * @Author: qyl
 * @Date: 2021/3/29 19:55
 */
public interface RuleService {

    /**
     * 发送心跳
     * @return 新规则
     */
    LimiterRule heartbeat(LimiterRule rateLimiterRule);

    /**
     * 更新规则（需更新版本号）
     */
    ResponseResult<Void> update(LimiterRule rateLimiterRule);

    /**
     * 查看规则
     * @return 规则集合
     */
    ResponseResult<List<LimiterRule>> getAllRule(String app, String id, int page, int limit);
}
