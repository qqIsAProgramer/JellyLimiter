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

    public static String getInstanceKey(LimiterRule limiterRule) {
        // 获取单个实例的 key
        return INSTANCE + limiterRule.getApp() + limiterRule.getId() + limiterRule.getName();
    }

    public static String getInstanceKeys(LimiterRule limiterRule) {
        // 获取所有实例的 key
        return INSTANCE + limiterRule.getApp() + limiterRule.getId() + "*";
    }

    public static String getLimiterRuleKey(LimiterRule limiterRule) {
        // 获取单个规则的 key
        return RULE + limiterRule.getApp() + limiterRule.getId();
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

    public static String getLockKey(LimiterRule limiterRule) {
        // 获取锁的 key
        return LOCK + limiterRule.getApp() + limiterRule.getId();
    }

    public static String getBucketKey(LimiterRule limiterRule) {
        // 获取令牌桶的 key
        return BUCKET + limiterRule.getApp() + limiterRule.getId();
    }
}
