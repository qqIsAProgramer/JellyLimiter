package com.qyl.starter.aspect;

import com.qyl.core.limiter.RateLimiter;
import com.qyl.core.observer.LimiterObserver;
import com.qyl.starter.annotation.Limiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @Author: qyl
 * @Date: 2021/3/30 17:10
 * 注解切片
 */
@Aspect
public class LimiterAspect {

    @Pointcut("@annotation(com.qyl.starter.annotation.Limiter)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(limiter)")
    public Object around(ProceedingJoinPoint pjp, Limiter limiter) throws Throwable {
        RateLimiter rateLimiter = LimiterObserver.getLimiterMap().get(limiter.id());
        if (rateLimiter.tryAcquire()) {
            return pjp.proceed();
        }
        Signature signature = pjp.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("This annotation can only be used in methods");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = pjp.getTarget();
        Method fallback = target.getClass().getMethod(limiter.fallback(), methodSignature.getParameterTypes());
        return fallback.invoke(target, pjp.getArgs());
    }
}
