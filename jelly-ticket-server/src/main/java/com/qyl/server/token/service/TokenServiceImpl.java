package com.qyl.server.token.service;

import com.qyl.common.entity.LimiterRule;
import com.qyl.common.utils.RedisKey;
import com.qyl.server.redisson.SingleRedisLock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @Author: qyl
 * @Date: 2021/3/29 20:24
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SingleRedisLock redisLock;

    @Override
    public Long getToken(LimiterRule limiterRule) {
        // 取出 Redis 中的令牌
        String value = stringRedisTemplate.opsForValue().get(RedisKey.getBucketKey(limiterRule));
        if (StringUtils.isEmpty(value)) {
            return 0L;
        }
        if (Long.parseLong(value) > 0) {
            redisLock.acquire(RedisKey.getLockKey(limiterRule));
            value = stringRedisTemplate.opsForValue().get(RedisKey.getBucketKey(limiterRule));
            if (!StringUtils.isEmpty(value)) {
                long token = Long.parseLong(value);
                long result;
                if (token <= 0) {
                    result = 0L;
                } else if (token >= limiterRule.getTokenRate()) {
                    stringRedisTemplate.opsForValue().decrement(RedisKey.getBucketKey(limiterRule), limiterRule.getTokenRate());
                    result = limiterRule.getTokenRate();
                } else {
                    stringRedisTemplate.opsForValue().decrement(RedisKey.getBucketKey(limiterRule), token);
                    result = token;
                }
                return result;
            }
            redisLock.release(RedisKey.getLockKey(limiterRule));
        }
        return 0L;
    }
}
