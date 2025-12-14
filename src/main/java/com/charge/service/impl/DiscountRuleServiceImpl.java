package com.charge.service.impl;

import com.charge.entity.DiscountRule;
import com.charge.mapper.DiscountRuleMapper;
import com.charge.service.DiscountRuleService;
import com.common.exception.BusinessException;

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class DiscountRuleServiceImpl implements DiscountRuleService {

    private final DiscountRuleMapper discountRuleMapper;

    public DiscountRuleServiceImpl(DiscountRuleMapper discountRuleMapper) {
        this.discountRuleMapper = discountRuleMapper;
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
        return rule;
    }

    @Override
    public void updateRule(DiscountRule rule) {
        if (rule.getId() == null) {
            throw new BusinessException("更新优惠规则ID不能为空");
        }
        // 允许部分字段为空，基础校验
        int rows = discountRuleMapper.updateById(rule);
        if (rows == 0) {
            throw new BusinessException("优惠规则不存在， id: " + rule.getId());
        }
    }

    @Override
    public void deleteRule(Long id) {
        if (id == null) {
            throw new BusinessException("删除优惠规则ID不能为空");
        }
        discountRuleMapper.deleteById(id);
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
}
