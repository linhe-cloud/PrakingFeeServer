package com.inout.entity.DTO;

/**
 * 工作端一次扫描中，单辆车的信息
 */
public class ScannedCarDTO {

    /**
     * 车牌号（必填）
     */
    private String plateNumber;

    /**
     * 车位编号 / 车位标识（可选）
     */
    private String slotNo;

    // TODO 如果需要车辆规格（vehicle_type）和使用性质（usage_nature），可以添加相应的字段

    public String getPlateNumber() {
        return plateNumber;
    }
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getSlotNo() {
        return slotNo;
    }
    public void setSlotNo(String slotNo) {
        this.slotNo = slotNo;
    }
}