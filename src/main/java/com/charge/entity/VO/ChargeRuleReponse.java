package com.charge.entity.VO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChargeRuleReponse {

    private Long id;

    private Long parkingIotId;

    private String ruleName;

    private String carType;

    private Integer chargeMode;

    private Integer freeMinutes;

    private Integer unitPrice;

    private Integer maxAmountPerDay;

    private Integer maxAmountPerSession;

    private LocalDate effectiveStartDate;

    private LocalDate effectiveEndDate;

    private String effectiveTimeRange;

    private Integer priority;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParkingIotId() {
        return parkingIotId;
    }

    public void setParkingIotId(Long parkingIotId) {
        this.parkingIotId = parkingIotId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Integer getChargeMode() {
        return chargeMode;
    }

    public void setChargeMode(Integer chargeMode) {
        this.chargeMode = chargeMode;
    }

    public Integer getFreeMinutes() {
        return freeMinutes;
    }

    public void setFreeMinutes(Integer freeMinutes) {
        this.freeMinutes = freeMinutes;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getMaxAmountPerDay() {
        return maxAmountPerDay;
    }

    public void setMaxAmountPerDay(Integer maxAmountPerDay) {
        this.maxAmountPerDay = maxAmountPerDay;
    }

    public Integer getMaxAmountPerSession() {
        return maxAmountPerSession;
    }

    public void setMaxAmountPerSession(Integer maxAmountPerSession) {
        this.maxAmountPerSession = maxAmountPerSession;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public String getEffectiveTimeRange() {
        return effectiveTimeRange;
    }

    public void setEffectiveTimeRange(String effectiveTimeRange) {
        this.effectiveTimeRange = effectiveTimeRange;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
