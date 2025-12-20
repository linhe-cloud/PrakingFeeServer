package com.charge.service.impl;

import com.charge.entity.DiscountRule;
import com.charge.mapper.DiscountRuleMapper;
import com.charge.service.DiscountRuleService;
import com.charge.service.ChargeCacheService;
import com.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiscountRuleServiceImpl implements DiscountRuleService {

    private final DiscountRuleMapper discountRuleMapper;
    private final ChargeCacheService chargeCacheService;

    public DiscountRuleServiceImpl(DiscountRuleMapper discountRuleMapper,
                                  ChargeCacheService chargeCacheService) {
        this.discountRuleMapper = discountRuleMapper;
        this.chargeCacheService = chargeCacheService;
    }

    @Override
    public DiscountRule createRule(DiscountRule rule) {
        if (rule.getRuleCode() == null || rule.getRuleCode().isEmpty()) {
            throw new BusinessException("规则编码不能为空");
        } 
        if (rule.getRuleName() == null || rule.getRuleName().isEmpty()) {
            throw new BusinessException("规则名称不能为空");
        }
        if (rule.getDiscountType() == null || rule.getDiscountType().isEmpty()) {
            throw new BusinessException("优惠类型不能为空");
        }
        if (rule.getDiscountValue() == null || rule.getDiscountValue() <= 0) {
            throw new BusinessException("优惠值必须大于0");
        }
        //默认启用
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        discountRuleMapper.insert(rule);
        
        // 写入缓存
        chargeCacheService.cacheDiscountRule(rule.getRuleCode(), rule);
        log.info("创建优惠规则成功并缓存: ruleCode={}, ruleId={}", rule.getRuleCode(), rule.getId());
        
        return rule;
    }

    @Override
    public void updateRule(DiscountRule rule) {
        if (rule.getId() == null) {
            throw new BusinessException("更新优惠规则ID不能为空");
        }
        // 先查询原规则，获取ruleCode
        DiscountRule oldRule = discountRuleMapper.selectById(rule.getId());
        if (oldRule == null) {
            throw new BusinessException("优惠规则不存在， id: " + rule.getId());
        }
        
        // 允许部分字段为空，基础校验
        int rows = discountRuleMapper.updateById(rule);
        if (rows == 0) {
            throw new BusinessException("优惠规则更新失败， id: " + rule.getId());
        }
        
        // 删除缓存，下次查询时重新加载
        chargeCacheService.evictDiscountRule(oldRule.getRuleCode());
        log.info("更新优惠规则并清除缓存: ruleCode={}, ruleId={}", oldRule.getRuleCode(), rule.getId());
    }

    @Override
    public void deleteRule(Long id) {
        if (id == null) {
            throw new BusinessException("删除优惠规则ID不能为空");
        }
        // 先查询规则信息，获取ruleCode
        DiscountRule rule = discountRuleMapper.selectById(id);
        if (rule != null) {
            // 删除数据库记录
            discountRuleMapper.deleteById(id);
            // 删除缓存
            chargeCacheService.evictDiscountRule(rule.getRuleCode());
            log.info("删除优惠规则并清除缓存: ruleCode={}, ruleId={}", rule.getRuleCode(), id);
        }
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        if (id == null) {
            throw new BusinessException("修改状态时id不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不合法，修改状态时status必须为0或1");
        }
        DiscountRule rule = discountRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException("优惠规则不存在， id: " + id);
        }
        rule.setStatus(status);
        discountRuleMapper.updateById(rule);
        
        // 删除缓存，下次查询时重新加载
        chargeCacheService.evictDiscountRule(rule.getRuleCode());
        log.info("修改优惠规则状态并清除缓存: ruleCode={}, status={}", rule.getRuleCode(), status);
    }

    @Override
    public DiscountRule getById(Long id) {
        if (id == null) {
            throw new BusinessException("查询优惠规则ID不能为空");
        }
        DiscountRule rule = discountRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException("优惠规则不存在， id: " + id);
        }
        return rule;
    }

    @Override
    public List<DiscountRule> listDiscountRule(Integer status) {
        return discountRuleMapper.selectList(status);
    }

    @Override
    public DiscountRule getEffectiveRuleByCode(String ruleCode) {
        if (ruleCode == null || ruleCode.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return discountRuleMapper.selectEffectiveByCode(ruleCode, now);
    }
}
