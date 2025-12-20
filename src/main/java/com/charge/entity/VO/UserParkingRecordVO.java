package com.charge.entity.VO;

import java.time.LocalDateTime;

/**
 * 用户停车记录详情（小程序端）
 */
public class UserParkingRecordVO {

    /**
     * 停车记录ID
     */
    private Long id;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车场ID
     */
    private Long parkingIotId;

    /**
     * 入场时间
     */
    private LocalDateTime inTime;

    /**
     * 出场时间（null表示还在停车中）
     */
    private LocalDateTime outTime;

    /**
     * 状态：IN-停车中，FINISHED-已完成
     */
    private String status;

    /**
     * 已支付金额（分）
     */
    private Long paidAmount;

    /**
     * 订单号（如果有关联订单）
     */
    private String orderNo;

    /**
     * 订单支付状态（如果有关联订单）
     */
    private String payStatus;

    /**
     * 订单金额（分）
     */
    private Long orderAmount;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Long getParkingIotId() {
        return parkingIotId;
    }
    public void setParkingIotId(Long parkingIotId) {
        this.parkingIotId = parkingIotId;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }
    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }
    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPaidAmount() {
        return paidAmount;
    }
    public void setPaidAmount(Long paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPayStatus() {
        return payStatus;
    }
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }
    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
    }
}
