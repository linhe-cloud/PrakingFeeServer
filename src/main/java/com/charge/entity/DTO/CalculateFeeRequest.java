package com.charge.entity.DTO;

import java.time.LocalDateTime;

/**
 * 计算费用请求参数（DTO）
 * 用于 Controller 接收前端的参数
 */
public class CalculateFeeRequest {
    /**
     * 入场记录 ID （必填）
     * 前端会传入一个已存在的入场记录
     */
    private Long inRecordId;

    /**
     * 出场时间（必填）
     * 示例：2025-12-09T10:30:00
     */
    private LocalDateTime exitTime;

    /**
     * 用户ID（用于会员优惠），可选
     */
    private Long userId;

    /**
     * 优惠规则编码（discount_rule.rule_code），可选
     */
    private String discountRuleCode;

    // 入场记录 ID
    public Long getInRecordId() {
        return inRecordId;
    }

    // 设置入场记录 ID
    public void setInRecordId(Long inRecordID) {
        this.inRecordId = inRecordID;
    }

    // 出场时间
    public LocalDateTime getExitTime() {
        return exitTime;
    }

    // 设置出场时间
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDiscountRuleCode() {
        return discountRuleCode;
    }
    public void setDiscountRuleCode(String discountRuleCode) {
        this.discountRuleCode = discountRuleCode;
    }
}
