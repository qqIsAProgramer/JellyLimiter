package com.qyl.server.rule.service;

import com.alibaba.fastjson.JSON;
import com.qyl.common.entity.LimiterRule;
import com.qyl.common.utils.RedisKey;
import com.qyl.server.exception.TicketServerException;
import com.qyl.server.redisson.SingleRedisLock;
import com.qyl.server.utils.ResponseEnum;
import com.qyl.server.utils.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: qyl
 * @Date: 2021/3/29 19:55
 */
@Service
public class RuleServiceImpl implements RuleService {

    private Logger logger = LoggerFactory.getLogger(RuleService.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SingleRedisLock redisLock;

    @Resource
    private ScheduledExecutorService scheduledExecutorService;

    private Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

    @Override
    public LimiterRule heartbeat(LimiterRule limiterRule) {
        // 1. 读取最新的规则
        LimiterRule newRule = readNewestRule(limiterRule);
        // 2. 标记实例装况
        stringRedisTemplate.opsForValue().set(RedisKey.getInstanceKey(newRule), RedisKey.INSTANCE, 10, TimeUnit.SECONDS);
        // 3. 实时更新实例状况
        updateInstanceNumber(newRule);
        // 4. 检查令牌桶状况
        putToken(newRule, limiterRule);
        return newRule;
    }

    /**
     * 读取最新规则
     */
    private LimiterRule readNewestRule(LimiterRule limiterRule) {
        String rule = stringRedisTemplate.opsForValue().get(RedisKey.getLimiterRuleKey(limiterRule));
        if (StringUtils.isEmpty(rule)) {
            // 添加规则
            stringRedisTemplate.opsForValue().set(RedisKey.getLimiterRuleKey(limiterRule), JSON.toJSONString(limiterRule), 10, TimeUnit.SECONDS);
        } else {
            // 规则延时
            stringRedisTemplate.expire(RedisKey.getLimiterRuleKey(limiterRule), 10, TimeUnit.SECONDS);
            // 读取最新
            LimiterRule cacheRule = JSON.parseObject(rule, LimiterRule.class);
            if (cacheRule.getVersion() > limiterRule.getVersion()) {
                cacheRule.setLimitName(limiterRule.getLimitName());
                return cacheRule;
            }
        }
        return limiterRule;
    }

    /**
     * 更新实例数
     */
    private void updateInstanceNumber(LimiterRule limiterRule) {
        Set<String> instances = stringRedisTemplate.keys(RedisKey.getInstanceKeys(limiterRule));
        if (instances != null) {
            limiterRule.setNumber(instances.size());
        }
    }

    /**
     * 执行存放令牌的任务
     */
    private void putToken(LimiterRule newRule, LimiterRule oldRule) {
        // 分配向桶里存放令牌的任务
        if (taskMap.containsKey(RedisKey.getBucketKey(newRule))) {
            if (newRule.getVersion() > oldRule.getVersion()) {
                ScheduledFuture<?> scheduledFuture = taskMap.get(RedisKey.getBucketKey(newRule));
                scheduledFuture.cancel(true);
            } else {
                return;
            }
        }
        // 执行任务
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            String rule = stringRedisTemplate.opsForValue().get(RedisKey.getLimiterRuleKey(newRule));
            if (StringUtils.isEmpty(rule)) {
                logger.debug("Task cancel: " + RedisKey.getLimiterRuleKey(newRule));
                taskMap.get(RedisKey.getBucketKey(newRule)).cancel(true);
                taskMap.remove(RedisKey.getBucketKey(newRule));
                return;
            }
            String value = stringRedisTemplate.opsForValue().get(RedisKey.getBucketKey(newRule));
            long token = value == null ? 0 : Long.parseLong(value);
            if (token < newRule.getCapacity()) {
                long delta = Math.min(newRule.getCapacity() - token, newRule.getTokenRate());
                stringRedisTemplate.opsForValue().increment(RedisKey.getBucketKey(newRule), delta);
            }
        }, 0, newRule.getPeriod(), newRule.getUnit());
        taskMap.put(RedisKey.getBucketKey(newRule), scheduledFuture);
    }

    @Override
    public ResponseResult<Void> update(LimiterRule limiterRule) {
        // 加锁
        redisLock.acquire(RedisKey.getLockKey(limiterRule));
        String ruleKey = RedisKey.getLimiterRuleKey(limiterRule);
        String rule = stringRedisTemplate.opsForValue().get(ruleKey);
        // 规则不存在
        if (StringUtils.isEmpty(rule)) {
            // 解锁
            redisLock.release(RedisKey.getLockKey(limiterRule));
            throw new TicketServerException("Rule is null", ResponseEnum.NULL_RESULT.getCode(), ResponseEnum.NULL_RESULT.getMsg());
        }
        // 更新版本号
        LimiterRule parseRule = JSON.parseObject(rule, LimiterRule.class);
        limiterRule.setVersion(parseRule.getVersion() + 1);
        // 更新规则
        stringRedisTemplate.opsForValue().set(ruleKey, JSON.toJSONString(limiterRule), 5, TimeUnit.SECONDS);
        // 解锁
        redisLock.release(RedisKey.getLockKey(limiterRule));
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<LimiterRule>> getAllRule(String app, String id, int page, int limit) {
        Set<String> ruleKeys = stringRedisTemplate.keys(RedisKey.getLimiterRuleKeys(app, id));
        if (ruleKeys == null) {
            throw new TicketServerException("Rule is null", ResponseEnum.NULL_RESULT.getCode(), ResponseEnum.NULL_RESULT.getMsg());
        }
        // 处理结果
        List<LimiterRule> list = new ArrayList<>();
        // 数据从 (page - 1) * limit 开始，共 limit 条
        ruleKeys.stream().skip((page - 1) * limit).limit(limit)
                .forEach(ruleKey -> {
                    String rule = stringRedisTemplate.opsForValue().get(ruleKey);
                    LimiterRule limiterRule = JSON.parseObject(rule, LimiterRule.class);
                    updateInstanceNumber(limiterRule);  // 为啥要更新呢...?
                    list.add(limiterRule);
                });
        return ResponseResult.ok(list);
    }
}
