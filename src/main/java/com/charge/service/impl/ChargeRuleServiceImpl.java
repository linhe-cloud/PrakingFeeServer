package com.charge.service.impl;

import com.charge.entity.ChargeRule;
import com.charge.mapper.ChargeRuleMapper;
import com.charge.service.ChargeRuleService;
import com.charge.service.ChargeCacheService;
import com.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChargeRuleServiceImpl implements ChargeRuleService {

    @Resource
    private ChargeRuleMapper chargeRuleMapper;
    
    @Resource
    private ChargeCacheService chargeCacheService;

    @Override
    public ChargeRule getApplicableRule(Long parkingIotId) {
        // 1. 先从缓存获取
        ChargeRule cachedRule = chargeCacheService.getChargeRule(parkingIotId);
        if (cachedRule != null) {
            // 空对象标记表示数据库中无此规则
            if (cachedRule.getId() == null) {
                log.debug("缓存命中（无规则）: parkingIotId={}", parkingIotId);
                return null;
            }
            log.debug("缓存命中: parkingIotId={}, ruleId={}", parkingIotId, cachedRule.getId());
            return cachedRule;
        }
        
        // 2. 缓存未命中，查询数据库
        log.debug("缓存未命中，查询数据库: parkingIotId={}", parkingIotId);
        List<ChargeRule> rules = chargeRuleMapper.selectValidRulesByParkingIot(parkingIotId, LocalDate.now());
        
        ChargeRule rule = null;
        if (rules != null && !rules.isEmpty()) {
            rule = rules.get(0);
        }
        
        // 3. 写入缓存（包括null值，防止缓存穿透）
        chargeCacheService.cacheChargeRule(parkingIotId, rule);
        
        return rule;
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
        
        // 删除缓存，下次查询时重新加载
        if (rule.getParkingIotId() != null) {
            chargeCacheService.evictChargeRule(rule.getParkingIotId());
            log.info("规则修改，删除缓存: parkingIotId={}", rule.getParkingIotId());
        }
    }

    @Override
    public List<ChargeRule> listRulesByParkingIot(Long parkingIotId) {
        return chargeRuleMapper.selectByParkingIot(parkingIotId);
    }

    @Override
    public void updateChargeRuleStatus(Long ruleId, Integer status) {
        ChargeRule rule = chargeRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new BusinessException("计费规则不存在,id=" + ruleId);
        }
        rule.setStatus(status);
        chargeRuleMapper.updateById(rule);
        
        // 删除缓存，下次查询时重新加载
        if (rule.getParkingIotId() != null) {
            chargeCacheService.evictChargeRule(rule.getParkingIotId());
            log.info("规则状态修改，删除缓存: parkingIotId={}, status={}", 
                rule.getParkingIotId(), status);
        }
    }
}
