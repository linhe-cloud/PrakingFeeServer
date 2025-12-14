package com.charge.controller.admin;

import com.charge.entity.ChargeRule;
import com.charge.entity.DTO.ChargeRuleRequest;
import com.charge.entity.VO.ChargeRuleReponse;
import com.charge.service.ChargeRuleService;
import com.common.result.Result;

import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 管理端-计费规则管理接口
 */
@RestController
@RequestMapping("/api/admin/chargeRule")
public class ChargeRuleAdminController {

    @Resource
    private ChargeRuleService chargeRuleService;

    /**
     * 新增或编辑计费规则
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody ChargeRuleRequest dto) {
        ChargeRule rule = new ChargeRule();
        rule.setId(dto.getId());
        rule.setParkingIotId(dto.getParkingIotId());
        rule.setRuleName(dto.getRuleName());
        rule.setCarType(dto.getCarType());
        rule.setChargeMode(dto.getChargeMode());
        rule.setFreeMinutes(dto.getFreeMinutes());
        rule.setUnitPrice(dto.getUnitPrice());
        rule.setMaxAmountPerDay(dto.getMaxAmountPerDay());
        rule.setMaxAmountPerSession(dto.getMaxAmountPerSession());
        rule.setEffectiveStartDate(dto.getEffectiveStartDate());
        rule.setEffectiveEndDate(dto.getEffectiveEndDate());
        rule.setEffectiveTimeRange(dto.getEffectiveTimeRange());
        rule.setPriority(dto.getPriority());
        rule.setStatus(dto.getStatus());

        chargeRuleService.saveRule(rule);
        return Result.success();
    }

    /**
     * 按车场查询规则列表
     */
    @GetMapping("/list")
    public Result<List<ChargeRuleReponse>> list(@RequestParam("parkingIotId") Long parkingIotId) {
        List<ChargeRule> rules = chargeRuleService.listRulesByParkingIot(parkingIotId);
        List<ChargeRuleReponse> voList = rules.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * 修改规则状态: 启用/禁用
     */
    @PostMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestParam("ruleId") Long ruleId,
                                     @RequestParam("status") Integer status) {
        chargeRuleService.updateChargeRuleStatus(ruleId, status);
        return Result.success();
    }

    private ChargeRuleReponse toVO(ChargeRule rule) {
        ChargeRuleReponse vo = new ChargeRuleReponse();
        vo.setId(rule.getId());
        vo.setParkingIotId(rule.getParkingIotId());
        vo.setRuleName(rule.getRuleName());
        vo.setCarType(rule.getCarType());
        vo.setChargeMode(rule.getChargeMode());
        vo.setFreeMinutes(rule.getFreeMinutes());
        vo.setUnitPrice(rule.getUnitPrice());
        vo.setMaxAmountPerDay(rule.getMaxAmountPerDay());
        vo.setMaxAmountPerSession(rule.getMaxAmountPerSession());
        vo.setEffectiveStartDate(rule.getEffectiveStartDate());
        vo.setEffectiveEndDate(rule.getEffectiveEndDate());
        vo.setEffectiveTimeRange(rule.getEffectiveTimeRange());
        vo.setPriority(rule.getPriority());
        vo.setStatus(rule.getStatus());
        vo.setCreatedTime(rule.getCreatedTime());
        vo.setUpdatedTime(rule.getUpdatedTime());
        return vo;
    }
}