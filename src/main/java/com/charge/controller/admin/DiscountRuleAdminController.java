package com.charge.controller.admin;

import org.springframework.web.bind.annotation.*;

import com.common.result.Result;
import com.charge.entity.DiscountRule;
import com.charge.service.DiscountRuleService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/charge")
public class DiscountRuleAdminController {

    private final DiscountRuleService discountRuleService;

    public DiscountRuleAdminController(DiscountRuleService discountRuleService) {
        this.discountRuleService = discountRuleService;
    }

    /**
     * 新增优惠规则
     */
    @PostMapping("/discount/create")
    public Result<DiscountRule> create(@RequestBody DiscountRule rule) {
        DiscountRule created = discountRuleService.createRule(rule);
        return Result.success(created);
    }

    /**
     * 更新优惠规则
     */
    @PostMapping("/discount/update")
    public Result<DiscountRule> update(@RequestBody DiscountRule rule) {
        discountRuleService.updateRule(rule);
        DiscountRule updated = discountRuleService.getById(rule.getId());
        return Result.success(updated);
    }

    /**
     * 删除优惠规则
     */
    @DeleteMapping("/discount/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        discountRuleService.deleteRule(id);
        return Result.success();
    }

    /**
     * 修改优惠规则状态：1-启用，0-禁用
     */
    @PostMapping("/discount/changeStatus")
    public Result<Void> changeStatus(@RequestParam("id") Long id,
                                     @RequestParam("status") Integer status) {
        discountRuleService.changeStatus(id, status);
        return Result.success();
    }

    /**
     * 按 id 查询优惠规则
     */
    @GetMapping("/discount/{id}")
    public Result<DiscountRule> detail(@PathVariable("id") Long id) {
        DiscountRule rule = discountRuleService.getById(id);
        return Result.success(rule);
    }

    /**
     * 优惠规则列表（管理端）
     * status：1-启用，0-禁用，null-全部
     */
    @GetMapping("/discount/list")
    public Result<List<DiscountRule>> listDiscountRule(
        @RequestParam(value = "status", required = false) Integer status) {
            List<DiscountRule> list = discountRuleService.listDiscountRule(status);
            return Result.success(list);
        }
    
}
