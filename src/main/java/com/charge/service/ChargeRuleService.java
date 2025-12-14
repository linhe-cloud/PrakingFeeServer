package com.charge.service;

import com.charge.entity.ChargeRule;

import java.util.List;

public interface ChargeRuleService {

    /**
     * 根据车场找到当前生效的规则
     */
    ChargeRule getApplicableRule(Long parkingIotId);

    /**
     * 按规则 + 总停车分钟数，计算应收金额（分）
     */
    long calculateAmount(ChargeRule rule, long totalMinutes);

    // ======= 管理端规则维护接口 ========

    /**
     * 更新或者新增计费规则
     */
    void saveRule(ChargeRule rule);

    /**
     * 查询某车场的所有规则（包含启用/停用）
     */
    List<ChargeRule> listRulesByParkingIot(Long parkingIotId);

    /**
     * 修改规则状态：1-启用，0-禁用
     */
    void updateChargeRuleStatus(Long ruleId, Integer status);
}
