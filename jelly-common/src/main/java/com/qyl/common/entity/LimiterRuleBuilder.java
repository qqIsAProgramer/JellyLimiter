package com.qyl.common.entity;

import com.qyl.common.enums.AcquireModel;
import com.qyl.common.enums.LimiterModel;
import com.qyl.common.enums.RuleAuthority;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: qyl
 * @Date: 2021/3/28 21:09
 * Builder 设计模式
 */
public class LimiterRuleBuilder {

    private LimiterRule limiterRule;

    public LimiterRuleBuilder(long permitNumber) {
        this.limiterRule = new LimiterRule(permitNumber);
    }

    /**
     * 设置 app name
     */
    public LimiterRuleBuilder setApp(String app) {
        this.limiterRule.setApp(app);
        return this;
    }
    /**
     * 限流规则名称
     */
    public LimiterRuleBuilder setId(String id) {
        this.limiterRule.setId(id);
        return this;
    }
    /**
     * 单位时间放入的令牌数
     */
    public LimiterRuleBuilder setTokenRate(long tokenRate) {
        this.limiterRule.setTokenRate(tokenRate);
        return this;
    }
    /**
     * 单位时间大小
     */
    public LimiterRuleBuilder setPeriod(long period) {
        this.limiterRule.setPeriod(period);
        return this;
    }
    /**
     * 时间单位
     */
    public LimiterRuleBuilder setUnit(TimeUnit unit) {
        this.limiterRule.setUnit(unit);
        return this;
    }
    /**
     * 黑白名单列表
     */
    public LimiterRuleBuilder setLimitUserList(List<String> limitUserList) {
        this.limiterRule.setLimitUserList(limitUserList);
        return this;
    }
    /**
     * 黑名单/白名单/无
     */
    public LimiterRuleBuilder setRuleAuthority(RuleAuthority ruleAuthority) {
        this.limiterRule.setRuleAuthority(ruleAuthority);
        return this;
    }
    /**
     * 控制行为：快速失败/阻塞
     */
    public LimiterRuleBuilder setAcquireModel(AcquireModel acquireModel) {
        this.limiterRule.setAcquireModel(acquireModel);
        return this;
    }
    /**
     * 部署方式：本地/分布式
     */
    public LimiterRuleBuilder setLimiterModel(LimiterModel limiterModel) {
        this.limiterRule.setLimiterModel(limiterModel);
        return this;
    }

    /**
     * 构建限流规则对象
     * @return LimiterRule
     */
    public LimiterRule build() {
        check(this.limiterRule);
        return this.limiterRule;
    }

    public static void check(LimiterRule limiterRule) {
        assert limiterRule.getCapacity() > 0;
        assert limiterRule.getTokenRate() >= 0;
        assert limiterRule.getPeriod() >= 0;
    }
}
