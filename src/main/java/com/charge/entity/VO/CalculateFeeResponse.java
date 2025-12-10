package com.charge.entity.VO;

import java.time.LocalDateTime;

/**
 * 计算停车费用的响应对象（VO）
 * 用于 Controller 返回给前端的结果
 */
public class CalculateFeeResponse {
    
    /**
     * 入场记录ID
     */
    private Long inRecordId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 停车时长（分钟）
     */
    private long parkingMinutes;

    /**
     * 应收金额（单位：分）
     */
    private long amount;

    /**
     * 使用的计费规则名称（可选）
     */
    private String feeRuleName;

    /**
     * 实际出场时间（可能会被系统纠偏）
     */
    private LocalDateTime realOutTime;

    public Long getInRecordId() {
        return inRecordId;
    }

    public void setInRecordId(Long inRecordId) {
        this.inRecordId = inRecordId;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public long getParkingMinutes() {
        return parkingMinutes;
    }

    public void setParkingMinutes(long parkingMinutes) {
        this.parkingMinutes = parkingMinutes;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getFeeRuleName() {
        return feeRuleName;
    }

    public void setFeeRuleName(String feeRuleName) {
        this.feeRuleName = feeRuleName;
    }

    public LocalDateTime getRealOutTime() {
        return realOutTime;
    }

    public void setRealOutTime(LocalDateTime realOutTime) {
        this.realOutTime = realOutTime;
    }
}

