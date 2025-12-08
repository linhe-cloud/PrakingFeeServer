package com.charge.entity;

import java.time.LocalDateTime;

public class ParkingIot {
    private Long id;
    private String code;
    private String name;

    // 计费相关
    private Integer billingType;
    private Integer unitPrice;
    private Integer maxDailyAmount;
    private Integer freeMinutes;

    // 运营
    private Integer status;
    private String openTime;
    private String remark;

    // 审计
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String createdBy;
    private String updatedBy;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getBillingType() {
        return billingType;
    }
    public void setBillingType(Integer billingType) {
        this.billingType = billingType;
    }
    public Integer getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }
    public Integer getMaxDailyAmount() {
        return maxDailyAmount;
    }
    public void setMaxDailyAmount(Integer maxDailyAmount) {
        this.maxDailyAmount = maxDailyAmount;
    }
    public Integer getFreeMinutes() {
        return freeMinutes;
    }
    public void setFreeMinutes(Integer freeMinutes) {
        this.freeMinutes = freeMinutes;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getOpenTime() {
        return openTime;
    }
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
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
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
