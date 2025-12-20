package com.charge.entity;

import java.time.LocalDateTime;

public class MemberRecharge {
    private Long id;
    private Long userId;
    private String rechargeNo;      // 充值单号
    private Long amount;            // 充值金额(分)
    private Long bonusAmount;       // 赠送金额(分)
    private Long totalAmount;       // 实际到账(分)
    private String payChannel;      // 支付渠道
    private Integer payStatus;      // 0-待支付，1-已支付，2-已退款
    private LocalDateTime payTime;
    private Integer memberUpgraded; // 是否触发会员升级：0-否，1-是
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRechargeNo() {
        return rechargeNo;
    }

    public void setRechargeNo(String rechargeNo) {
        this.rechargeNo = rechargeNo;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(Long bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public Integer getMemberUpgraded() {
        return memberUpgraded;
    }

    public void setMemberUpgraded(Integer memberUpgraded) {
        this.memberUpgraded = memberUpgraded;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
