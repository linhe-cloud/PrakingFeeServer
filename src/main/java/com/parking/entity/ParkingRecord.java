package com.parking.entity;

import java.time.LocalDateTime;

public class ParkingRecord {

    private Long id;
    private String plateNumber;
    private String vehicleType;
    private String usageNature;
    private Long parkingIotId;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private Long paidAmount;
    private String status;
    private String remark;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

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

    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getUsageNature() {
        return usageNature;
    }
    public void setUsageNature(String usageNature) {
        this.usageNature = usageNature;
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

    public Long getPaidAmount() {
        return paidAmount;
    }
    public void setPaidAmount(Long paidAmount) {
        this.paidAmount = paidAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
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
