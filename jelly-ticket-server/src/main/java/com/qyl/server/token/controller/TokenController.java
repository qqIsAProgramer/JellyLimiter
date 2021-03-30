package com.qyl.server.token.controller;

import com.alibaba.fastjson.JSON;
import com.qyl.common.entity.LimiterRule;
import com.qyl.server.token.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: qyl
 * @Date: 2021/3/29 20:23
 */
@RestController
public class TokenController {

    @Resource
    private TokenService tokenService;

    /**
     * 从 Redis 中获取 token
     */
    @PostMapping("/token")
    public String getToken(@RequestParam("data") String rule) {
        LimiterRule limiterRule = JSON.parseObject(rule, LimiterRule.class);
        return JSON.toJSONString(tokenService.getToken(limiterRule));
    }
}
