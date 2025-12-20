package com.charge.entity.VO;

import java.time.LocalDateTime;

/**
 * 用户订单详情（小程序端）
 */
public class UserOrderVO {

    private Long id;
    private String orderNo;
    private String plateNumber;
    private Long parkingIotId;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private Long amount;
    private String payStatus;
    private String payChannel;
    private LocalDateTime payTime;
    private String feeRuleName;
    private LocalDateTime createTime;

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
}
