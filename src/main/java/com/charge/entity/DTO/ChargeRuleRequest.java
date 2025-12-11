package com.charge.entity.DTO;

import java.time.LocalDate;

/**
 * 管理端-计费规则入参 DTO
 */
public class ChargeRuleRequest {

    private Long id;

    private Long parkingIotId;

    private String ruleName;

    private String carType;

    private Integer chargeMode;

    private Integer freeMinutes;

    /**
     * 单价(分),例如 500 表示 5 元/小时
     */
    private Integer unitPrice;

    /**
     * 每日封顶金额(分)
     */
    private Integer maxAmountPerDay;

    /**
     * 单次封顶金额(分)
     */
    private Integer maxAmountPerSession;

    /**
     * 生效开始日期
     */
    private LocalDate effectiveStartDate;

    /**
     * 生效结束日期
     */
    private LocalDate effectiveEndDate;

    /**
     * 生效时间段,例如 "08:00-18:00"
     */
    private String effectiveTimeRange;

    /**
     * 优先级,值越大优先级越高
     */
    private Integer priority;

    /**
     * 状态: 1=启用,0=禁用
     */
    private Integer status;

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
}