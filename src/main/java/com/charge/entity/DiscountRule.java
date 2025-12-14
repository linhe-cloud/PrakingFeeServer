package com.charge.entity;

import java.time.LocalDateTime;

public class DiscountRule {

    private Long id;
    private String ruleCode;
    private String ruleName;
    private String description;

    // PERCENT / FIXED
    private String discountType;
    // 百分比(如80表示8折) 或 金额(分)
    private Integer discountValue;
    // 单笔最大优惠金额(分)，可空
    private Integer maxDiscount;

    // 1-启用，0-禁用
    private Integer status;
    private LocalDateTime effectiveStart;
    private LocalDateTime effectiveEnd;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountType() {
        return discountType;
    }
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Integer getDiscountValue() {
        return discountValue;
    }
    public void setDiscountValue(Integer discountValue) {
        this.discountValue = discountValue;
    }

    public Integer getMaxDiscount() {
        return maxDiscount;
    }
    public void setMaxDiscount(Integer maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getEffectiveStart() {
        return effectiveStart;
    }
    public void setEffectiveStart(LocalDateTime effectiveStart) {
        this.effectiveStart = effectiveStart;
    }

    public LocalDateTime getEffectiveEnd() {
        return effectiveEnd;
    }
    public void setEffectiveEnd(LocalDateTime effectiveEnd) {
        this.effectiveEnd = effectiveEnd;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}