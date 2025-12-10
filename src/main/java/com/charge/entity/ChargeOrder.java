package com.charge.entity;

import java.time.LocalDateTime;


public class ChargeOrder {

    private Long id;
    private Long inRecordId;
    private Long amount;            //金额（分）
    private String payStatus;       // UnPAID, PAID
    private LocalDateTime outTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getOutTime() {
        return outTime;
    }
    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime; 
    }
}
