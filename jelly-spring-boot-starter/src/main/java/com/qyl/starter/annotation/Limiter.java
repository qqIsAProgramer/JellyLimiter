package com.qyl.starter.annotation;

import java.lang.annotation.*;

/**
 * @Author: qyl
 * @Date: 2021/3/30 17:08
 * 核心注解
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limiter {

    /**
     * RateLimiter id
     */
    String id() default "";

    /**
     * Call this function after denying service
     */
    String fallback() default "";
}
