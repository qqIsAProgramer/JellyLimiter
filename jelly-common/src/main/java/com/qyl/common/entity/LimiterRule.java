package com.qyl.common.entity;

import com.qyl.common.enums.AcquireModel;
import com.qyl.common.enums.LimiterModel;
import com.qyl.common.enums.RuleAuthority;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: qyl
 * @Date: 2021/3/28 21:09
 */
@Data
public class LimiterRule implements Comparable<LimiterRule> {
    // Application related
    /**
     * 项目应用名称（默认 application）
     */
    private String app = "application";
    /**
     * 限流规则名称（对应 @RateLimiter(value = "${id}")）
     */
    private String id = "id";
    /**
     * 相同的限流规则，不同的实例标识（不需要用户配置）
     */
    private String name;
    /**
     * 是否开启限流（默认 true）
     */
    private boolean enable = true;

    // QPS related
    /**
     * 单位时间生产的 token
     */
    private long tokenRate;
    /**
     * 单位时间大小（默认 1）
     */
    private long period = 1;
    /**
     * 单位时间的单位（默认 second）
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    // Bucket related
    /**
     * 令牌桶的容量
     */
    private long capacity;

    // Enums related
    /**
     * 限流器模型：单体式/分布式（默认单体式）
     */
    private LimiterModel limiterModel = LimiterModel.MONOLITHIC;
    /**
     * 控制行为：快速失败/阻塞（默认快速失败）
     */
    private AcquireModel acquireModel = AcquireModel.FAIL_FAST;
    /**
     * 黑名单/白名单/无（默认无）
     */
    private RuleAuthority ruleAuthority = RuleAuthority.NULL;
    /**
     * 黑白名单列表
     */
    private List<String> limitUserList = new ArrayList<>();

    // System related
    /**
     * 实例数（不需要用户配置）
     */
    private int number;
    /**
     * 版本号（不需要用户配置）
     */
    private int version;

    public LimiterRule(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public int compareTo(LimiterRule o) {
        if (this.version < o.getVersion()) {
            return -1;
        } else if (this.version == o.getVersion()) {
            return 0;
        }
        return 1;
    }
}
