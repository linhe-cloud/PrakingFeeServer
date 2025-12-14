package com.charge.controller.admin;

import com.charge.entity.ChargeOrder;
import com.charge.entity.ParkingRecord;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.mapper.ParkingRecordMapper;
import com.common.exception.BusinessException;
import com.common.result.PageResult;
import com.common.result.Result;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 分页查询订单列表（管理端查看）
     */
    @GetMapping("/orders")
    public Result<PageResult<ChargeOrder>> listOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "payStatus", required = false) String payStatus) {
                
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        } 

        long current = page.longValue();
        long pageSize = size.longValue();
        long offset = (current - 1) * pageSize;

        Long total = chargeOrderMapper.countByCondition(payStatus);
        List<ChargeOrder> rows = chargeOrderMapper.selectPage(payStatus, offset, pageSize);

        PageResult<ChargeOrder> pageResult = PageResult.build(rows, total, current, pageSize);
        return Result.success(pageResult);
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