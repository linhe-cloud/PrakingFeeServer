package com.charge.controller.miniapp;

import com.charge.entity.ChargeOrder;
import com.parking.entity.ParkingRecord;
import com.charge.entity.VO.UserOrderVO;
import com.charge.mapper.ChargeOrderMapper;
import com.parking.mapper.ParkingRecordMapper;
import com.common.exception.BusinessException;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序端：订单接口
 */
@RestController
@RequestMapping("/api/miniapp/order")
public class MiniappOrderController {

    private final ChargeOrderMapper chargeOrderMapper;
    private final ParkingRecordMapper parkingRecordMapper;

    public MiniappOrderController(ChargeOrderMapper chargeOrderMapper,
                                 ParkingRecordMapper parkingRecordMapper) {
        this.chargeOrderMapper = chargeOrderMapper;
        this.parkingRecordMapper = parkingRecordMapper;
    }

    /**
     * 根据订单号查询订单详情
     */
    @GetMapping("/detail")
    public Result<UserOrderVO> getOrderDetail(@RequestParam("orderNo") String orderNo) {
        if (orderNo == null || orderNo.isEmpty()) {
            throw new BusinessException("订单号不能为空");
        }

        // 这里需要一个按orderNo查询的方法，暂时用id查
        // 你可以在 ChargeOrderMapper 里补充 selectByOrderNo 方法
        throw new BusinessException("待实现：按订单号查询");
    }

    /**
     * 根据停车记录ID查询关联订单
     */
    @GetMapping("/byRecord")
    public Result<UserOrderVO> getOrderByRecord(@RequestParam("recordId") Long recordId) {
        if (recordId == null) {
            throw new BusinessException("停车记录ID不能为空");
        }

        ChargeOrder order = chargeOrderMapper.selectByInRecordId(recordId);
        if (order == null) {
            throw new BusinessException("该停车记录暂无订单");
        }

        UserOrderVO vo = convertToVO(order);
        return Result.success(vo);
    }

    /**
     * 转换为VO（包含停车记录信息）
     */
    private UserOrderVO convertToVO(ChargeOrder order) {
        UserOrderVO vo = new UserOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setAmount(order.getAmount());
        vo.setPayStatus(order.getPayStatus());
        vo.setPayChannel(order.getPayChannel());
        vo.setPayTime(order.getPayTime());
        vo.setOutTime(order.getOutTime());
        vo.setFeeRuleName(order.getFeeRuleName());
        vo.setCreateTime(order.getCreateTime());

        // 关联停车记录
        if (order.getInRecordId() != null) {
            ParkingRecord record = parkingRecordMapper.selectById(order.getInRecordId());
            if (record != null) {
                vo.setPlateNumber(record.getPlateNumber());
                vo.setParkingIotId(record.getParkingIotId());
                vo.setInTime(record.getInTime());
            }
        }

        return vo;
    }
}
