package com.charge.controller.miniapp;

import com.charge.entity.ChargeOrder;
import com.parking.entity.ParkingRecord;
import com.charge.entity.VO.UserParkingRecordVO;
import com.charge.mapper.ChargeOrderMapper;
import com.parking.mapper.ParkingRecordMapper;
import com.common.exception.BusinessException;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序端：停车记录接口
 */
@RestController
@RequestMapping("/api/miniapp/parking")
public class MiniappParkingController {

    private final ParkingRecordMapper parkingRecordMapper;
    private final ChargeOrderMapper chargeOrderMapper;

    public MiniappParkingController(ParkingRecordMapper parkingRecordMapper,
                                   ChargeOrderMapper chargeOrderMapper) {
        this.parkingRecordMapper = parkingRecordMapper;
        this.chargeOrderMapper = chargeOrderMapper;
    }

    /**
     * 查询用户当前停车中的记录（按车牌）
     */
    @GetMapping("/current")
    public Result<List<UserParkingRecordVO>> getCurrentParking(@RequestParam("plateNumber") String plateNumber) {
        if (plateNumber == null || plateNumber.isEmpty()) {
            throw new BusinessException("车牌号不能为空");
        }

        List<ParkingRecord> records = parkingRecordMapper.selectCurrentByPlateNumber(plateNumber);
        List<UserParkingRecordVO> voList = new ArrayList<>();

        for (ParkingRecord record : records) {
            UserParkingRecordVO vo = convertToVO(record);
            voList.add(vo);
        }

        return Result.success(voList);
    }

    /**
     * 查询用户停车历史（按车牌）
     */
    @GetMapping("/history")
    public Result<List<UserParkingRecordVO>> getParkingHistory(@RequestParam("plateNumber") String plateNumber) {
        if (plateNumber == null || plateNumber.isEmpty()) {
            throw new BusinessException("车牌号不能为空");
        }

        List<ParkingRecord> records = parkingRecordMapper.selectByPlateNumber(plateNumber);
        List<UserParkingRecordVO> voList = new ArrayList<>();

        for (ParkingRecord record : records) {
            UserParkingRecordVO vo = convertToVO(record);
            voList.add(vo);
        }

        return Result.success(voList);
    }

    /**
     * 查询单条停车记录详情
     */
    @GetMapping("/detail")
    public Result<UserParkingRecordVO> getDetail(@RequestParam("recordId") Long recordId) {
        if (recordId == null) {
            throw new BusinessException("停车记录ID不能为空");
        }

        ParkingRecord record = parkingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("停车记录不存在");
        }

        UserParkingRecordVO vo = convertToVO(record);
        return Result.success(vo);
    }

    /**
     * 转换为VO（包含关联订单信息）
     */
    private UserParkingRecordVO convertToVO(ParkingRecord record) {
        UserParkingRecordVO vo = new UserParkingRecordVO();
        vo.setId(record.getId());
        vo.setPlateNumber(record.getPlateNumber());
        vo.setParkingIotId(record.getParkingIotId());
        vo.setInTime(record.getInTime());
        vo.setOutTime(record.getOutTime());
        vo.setStatus(record.getStatus());
        vo.setPaidAmount(record.getPaidAmount());

        // 查询关联订单（如果有）
        ChargeOrder order = chargeOrderMapper.selectByInRecordId(record.getId());
        if (order != null) {
            vo.setOrderNo(order.getOrderNo());
            vo.setPayStatus(order.getPayStatus());
            vo.setOrderAmount(order.getAmount());
        }

        return vo;
    }
}
