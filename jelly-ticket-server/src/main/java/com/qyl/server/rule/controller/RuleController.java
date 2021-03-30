package com.qyl.server.rule.controller;

import com.alibaba.fastjson.JSON;
import com.qyl.common.entity.LimiterRule;
import com.qyl.common.entity.LimiterRuleBuilder;
import com.qyl.server.rule.service.RuleService;
import com.qyl.server.utils.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: qyl
 * @Date: 2021/3/29 19:55
 */
@RestController
public class RuleController {

    @Resource
    private RuleService ruleService;

    private Logger logger = LoggerFactory.getLogger(RuleController.class);

    /**
     * 发送心跳
     * @return 新规则
     */
    @PostMapping("/heartbeat")
    public String heartbeat(@RequestParam("data") String rule) {
        logger.info("heartbeat for the rule: " + rule);
        LimiterRule limiterRule = JSON.parseObject(rule, LimiterRule.class);
        return JSON.toJSONString(ruleService.heartbeat(limiterRule));
    }

    /**
     * 更新规则
     * @param rule 新规则
     */
    @PutMapping("/rule/update")
    public ResponseResult<Void> update(@RequestParam("data") String rule) {
        LimiterRule limiterRule = JSON.parseObject(rule, LimiterRule.class);
        // 检查新规则
        LimiterRuleBuilder.check(limiterRule);
        return ruleService.update(limiterRule);
    }

    /**
     * 查看规则
     * @return 规则集合
     */
    @GetMapping("/rule/all")
    public ResponseResult<List<LimiterRule>> getAllRule(String app, String id, int page, int limit) {
        // 分页实现
        return ruleService.getAllRule(app, id, page, limit);
    }
}
