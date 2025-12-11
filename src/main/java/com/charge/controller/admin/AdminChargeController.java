package com.charge.controller.admin;

import com.charge.entity.ChargeOrder;
import com.charge.entity.ParkingRecord;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.mapper.ParkingRecordMapper;
import com.common.exception.BusinessException;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/charge") 
public class AdminChargeController {

    private final ChargeOrderMapper chargeOrderMapper;
    private final ParkingRecordMapper parkingRecordMapper;

    public AdminChargeController(ChargeOrderMapper chargeOrderMapper,
                                 ParkingRecordMapper parkingRecordMapper) {
        this.chargeOrderMapper = chargeOrderMapper;
        this.parkingRecordMapper = parkingRecordMapper;
    }

    /**
     * 查询订单详情（管理端查看）
     */
    @GetMapping("/orders/{orderId}")
    public Result<ChargeOrder> getOrderDetail(@PathVariable("orderId") Long orderId) {
        ChargeOrder order = chargeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在，id=" + orderId);
        }
        return Result.success(order);
    }

    /**
     * 查询停车记录详情（管理端查看）
     */
    @GetMapping("/parking-records/{recordId}")
    public Result<ParkingRecord> getParkingRecordDetail(@PathVariable("recordId") Long recordId) {
        ParkingRecord record = parkingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("入场记录不存在，id=" + recordId);
        }
        return Result.success(record);
    }

}