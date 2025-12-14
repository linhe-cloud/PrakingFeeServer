package com.charge.service;

import com.charge.entity.DiscountRule;

import java.util.List;
    

public interface DiscountRuleService {

    /**
     * 新增优惠规则
     */
    DiscountRule createRule(DiscountRule rule);

    /**
     * 更新优惠规则（按 id）
     */
    void updateRule(DiscountRule rule);

    /**
     * 删除优惠规则
     */
    void deleteRule(Long id);

    /**
     * 修改优惠规则状态：1-启用，0-禁用
     */
    void changeStatus(Long id , Integer status);

    /**
     * 按 id 查询优惠规则
     */
    DiscountRule getById(Long id);

    /**
     * 按查询状态优惠规则列表
     * status 为 null 查询全部
     */
    List<DiscountRule> listDiscountRule(Integer status);
}
