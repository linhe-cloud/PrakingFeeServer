package com.inout.controller;

import com.parking.entity.ParkingRecord;
import com.parking.mapper.ParkingRecordMapper;
import com.common.exception.BusinessException;
import com.common.result.Result;
import com.inout.entity.DTO.ScanReportRequest;
import com.inout.entity.DTO.ScannedCarDTO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 工作端巡检接口：根据扫描结果自动推算入场 / 出场
 */
@RestController
@RequestMapping("/api/work/scan")
public class WorkScanController {

    private final ParkingRecordMapper parkingRecordMapper;

    public WorkScanController(ParkingRecordMapper parkingRecordMapper) {
        this.parkingRecordMapper = parkingRecordMapper;
    }

    /**
     * 巡检上报接口：
     * 1. 本次扫描看到的车中，新的车 -> 生成入场记录
     * 2. 上次还在场，这次没看到的车 -> 认为已离场，补出场时间
     */
    @PostMapping("/report")
    public Result<Map<String, Object>> report(@RequestBody ScanReportRequest request) {
        // 1. 基本校验
        if (request.getZoneId() == null) {
            throw new BusinessException("zoneId(路段ID)不能为空");
        }
        if (request.getScanTime() == null) {
            throw new BusinessException("scanTime(扫描时间)不能为空");
        }
        if (request.getCars() == null) {
            throw new BusinessException("cars(车辆列表)不能为空");
        }

        Long zoneId = request.getZoneId();
        LocalDateTime scanTime = request.getScanTime();
        List<ScannedCarDTO> cars = request.getCars();

        // 2. 取当前扫描的车牌集合
        Set<String> currentPlateSet = new HashSet<>();
        for (ScannedCarDTO car : cars) {
            if (car.getPlateNumber() != null && !car.getPlateNumber().isEmpty()) {
                currentPlateSet.add(car.getPlateNumber());
            }
        }

        // 3. 查询当前路段所有“在场”的记录
        List<ParkingRecord> currentRecords = parkingRecordMapper.selectCurrentByParkingIotId(zoneId);
        Map<String, ParkingRecord> plateToRecord = new HashMap<>();
        for (ParkingRecord record : currentRecords) {
            if (record.getPlateNumber() != null) {
                plateToRecord.put(record.getPlateNumber(), record);
            }
        }

        int newInCount = 0;
        int leaveCount = 0;

        // 4. 处理“消失的车” -> 出场（从在场列表里有，但本次扫描没看到）
        for (ParkingRecord record : currentRecords) {
            String plate = record.getPlateNumber();
            if (!currentPlateSet.contains(plate)) {
                // 认为这辆车已经离场，补出场时间
                record.setOutTime(scanTime);
                record.setStatus("FINISHED");
                parkingRecordMapper.updateById(record);
                leaveCount++;
            }
        }

        // 5. 处理“新出现的车” -> 入场（本次扫描看到，但之前不在场）
        for (ScannedCarDTO car : cars) {
            String plate = car.getPlateNumber();
            if (plate == null || plate.isEmpty()) {
                continue;
            }
            // 之前已经在场的车，不需要新建记录
            if (plateToRecord.containsKey(plate)) {
                continue;
            }

            // 新车 -> 创建入场记录
            ParkingRecord newRecord = new ParkingRecord();
            newRecord.setPlateNumber(plate);
            newRecord.setParkingIotId(zoneId);
            newRecord.setInTime(scanTime);
            newRecord.setOutTime(null);
            newRecord.setPaidAmount(null);
            newRecord.setStatus("IN");

            // 备注里可以带上车位和巡检人信息
            StringBuilder remark = new StringBuilder("工作端巡检入场");
            if (car.getSlotNo() != null && !car.getSlotNo().isEmpty()) {
                remark.append("，车位：").append(car.getSlotNo());
            }
            if (request.getInspectorName() != null && !request.getInspectorName().isEmpty()) {
                remark.append("，巡检人：").append(request.getInspectorName());
            }
            newRecord.setRemark(remark.toString());

            parkingRecordMapper.insert(newRecord);
            newInCount++;
        }

        // 6. 返回本次处理的简单统计
        Map<String, Object> data = new HashMap<>();
        data.put("zoneId", zoneId);
        data.put("scanTime", scanTime);
        data.put("newInCount", newInCount);
        data.put("leaveCount", leaveCount);
        data.put("currentScanCarCount", currentPlateSet.size());

        return Result.success(data);
    }
}