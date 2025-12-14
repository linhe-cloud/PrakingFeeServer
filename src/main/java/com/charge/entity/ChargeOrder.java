package com.charge.entity;

import java.time.LocalDateTime;


public class ChargeOrder {

    private Long id;
    private String orderNo;
    private Long inRecordId;
    private Long amount;            //金额（分）
    private String payStatus;       // UNPAID, PAID
    private String payChannel;
    private LocalDateTime payTime;
    private LocalDateTime outTime;
    private String feeRuleName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getInRecordId() {
        return inRecordId;
    }
    public void setInRecordId(Long inRecordId) {
        this.inRecordId = inRecordId;
    }

    public Long getAmount() {
        return amount;
    }
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPayStatus() {
        return payStatus;
    }
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayChannel() {
        return payChannel;
    }
    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }
    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }
    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public String getFeeRuleName() {
        return feeRuleName;
    }
    public void setFeeRuleName(String feeRuleName) {
        this.feeRuleName = feeRuleName;
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
