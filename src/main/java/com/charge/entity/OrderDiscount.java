package com.charge.entity;

import java.time.LocalDateTime;

public class OrderDiscount {

    private Long id;
    private Long orderId;
    private String sourceType;     // MEMBER / RULE / COUPON / PROMO ...
    private String ruleCode;
    private String ruleName;
    private String discountType;   // PERCENT / FIXED / FREE ...
    private Integer discountValue; // 百分比(80=8折)/金额(分)/其他
    private Long discountAmount;   // 实际优惠金额(分)
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getSourceType() {
        return sourceType;
    }
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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

    public Long getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
