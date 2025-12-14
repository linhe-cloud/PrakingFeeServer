package com.charge.service.impl;

import com.charge.entity.ChargeRule;
import com.charge.mapper.ChargeRuleMapper;
import com.charge.service.ChargeRuleService;
import com.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class ChargeRuleServiceImpl implements ChargeRuleService {

    @Resource
    private ChargeRuleMapper chargeRuleMapper;

    @Override
    public ChargeRule getApplicableRule(Long parkingIotId) {
        List<ChargeRule> rules = chargeRuleMapper.selectValidRulesByParkingIot(parkingIotId, LocalDate.now());
        if (rules == null || rules.isEmpty()) {
            return null;
        }
        return rules.get(0);
    }

    @Override
    public long calculateAmount(ChargeRule rule, long totalMinutes) {
        if (rule == null) {
            return -1L;
        }

        // 1. 首免分钟数
        long chargeMinutes = totalMinutes - rule.getFreeMinutes();
        if (chargeMinutes < 0) {
            return 0L;
        }

        // 2. 目前只支持按小时统一单价，向上取整小时
        long hours = (chargeMinutes + 59) / 60;
        long amount = hours * rule.getUnitPrice();

        // 3. 单次封顶
        if (rule.getMaxAmountPerSession() != null
                && amount > rule.getMaxAmountPerSession()) {
            amount = rule.getMaxAmountPerSession();
        }

        // 4. 每日封顶（这里先不实现跨天逻辑，留给后续优化）
        // TODO: 按需扩展

        return amount;
    }

    @Override
    public void saveRule(ChargeRule rule) {
        if (rule.getId() == null) {
            if (rule.getStatus() == null) {
                rule.setStatus(1);
            }
            chargeRuleMapper.insert(rule);
        } else {
            chargeRuleMapper.updateById(rule);
        }
    }

    @Override
    public List<ChargeRule> listRulesByParkingIot(Long parkingIotId) {
        return chargeRuleMapper.selectValidRulesByParkingIot(parkingIotId, LocalDate.now());
    }

    @Override
    public void updateChargeRuleStatus(Long ruleId, Integer status) {
        ChargeRule rule = chargeRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException("计费规则不存在,id=" + ruleId);
        }
        rule.setStatus(status);
        chargeRuleMapper.updateById(rule);
    }
}
