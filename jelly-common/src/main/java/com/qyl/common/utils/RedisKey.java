package com.qyl.common.utils;

import com.qyl.common.entity.LimiterRule;

/**
 * @Author: qyl
 * @Date: 2021/3/30 9:31
 */
public class RedisKey {

    public static final String INSTANCE = "$INSTANCE$";  // 实例前缀
    private static final String RULE = "$RULE$";  // 规则前缀
    private static final String LOCK = "$LOCK$";  // 锁前缀
    private static final String BUCKET = "$BUCKET$";  // 令牌桶前缀
    private static final String BUCKET_PRINCIPAL = "$BUCKET_PRINCIPAL$";  // 令牌桶负责的线程

    public static String getInstanceKey(LimiterRule rateLimiterRule) {
        // 获取单个实例的 key
        return INSTANCE + rateLimiterRule.getApp() + rateLimiterRule.getId() + rateLimiterRule.getName();
    }

    public static String getInstanceKeys(LimiterRule rateLimiterRule) {
        // 获取所有实例的 key
        return INSTANCE + rateLimiterRule.getApp() + rateLimiterRule.getId() + "*";
    }

    public static String getLimiterRuleKey(LimiterRule rateLimiterRule) {
        // 获取单个规则的 key
        return RULE + rateLimiterRule.getApp() + rateLimiterRule.getId();
    }

    public static String getLimiterRuleKeys(String app, String id) {
        // 获取所有规则的 key
        StringBuilder builder = new StringBuilder();
        if (app == null || app.length() == 0) {
            builder.append("*");
        } else {
            builder.append(app);
            if (id != null && id.length() != 0) {
                builder.append(id);
            }
        }
        return RULE + builder.toString();
    }

    public static String getLockKey(LimiterRule rateLimiterRule) {
        // 获取锁的 key
        return LOCK + rateLimiterRule.getApp() + rateLimiterRule.getId();
    }

    public static String getBucketKey(LimiterRule rateLimiterRule) {
        // 获取令牌桶的 key
        return BUCKET + rateLimiterRule.getApp() + rateLimiterRule.getId();
    }

    public static String getBucketPrincipalKey(LimiterRule rateLimiterRule) {
        // 获取负责一个令牌桶的线程的 key
        return BUCKET_PRINCIPAL + rateLimiterRule.getApp() + rateLimiterRule.getId();
    }
}
