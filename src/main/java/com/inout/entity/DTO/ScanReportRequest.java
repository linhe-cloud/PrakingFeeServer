package com.inout.entity.DTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作人员一次巡检上报请求
 */
public class ScanReportRequest {

    /**
     * 巡检人ID（可选，看你是否需要）
     */
    private Long inspectorId;

    /**
     * 巡检人姓名（可选）
     */
    private String inspectorName;

    /**
     * 路段 / 区域ID（必填，对应 parking_record.parking_lot_id）
     */
    private Long zoneId;

    /**
     * 扫描时间（必填）
     */
    private LocalDateTime scanTime;

    /**
     * 本次扫描看到的车辆列表（必填）
     */
    private List<ScannedCarDTO> cars;

    public Long getInspectorId() {
        return inspectorId;
    }
    public void setInspectorId(Long inspectorId) {
        this.inspectorId = inspectorId;
    }

    public String getInspectorName() {
        return inspectorName;
    }
    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public Long getZoneId() {
        return zoneId;
    }
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public LocalDateTime getScanTime() {
        return scanTime;
    }
    public void setScanTime(LocalDateTime scanTime) {
        this.scanTime = scanTime;
    }

    public List<ScannedCarDTO> getCars() {
        return cars;
    }
    public void setCars(List<ScannedCarDTO> cars) {
        this.cars = cars;
    }
}