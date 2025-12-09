package com.charge.service.impl;

import com.charge.entity.DTO.CalculateFeeRequest;
import com.charge.entity.VO.CalculateFeeResponse;
import com.charge.entity.ParkingRecord;
import com.charge.entity.ChargeOrder;
import com.charge.mapper.ParkingRecordMapper;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.service.ChargeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 计费服务实现类
 * 核心业务逻辑都写在这里
 */
@Service
public class ChargeServiceImpl implements ChargeService {

    private final ParkingRecordMapper parkingRecordMapper;
    private final ChargeOrderMapper chargeOrderMapper;

    public ChargeServiceImpl(ParkingRecordMapper parkingRecordMapper,
                             ChargeOrderMapper chargeOrderMapper) {
        this.parkingRecordMapper = parkingRecordMapper;
        this.chargeOrderMapper = chargeOrderMapper;
    }

    /**
     * 整个出场计费流程放在一个事务里
     */
    @Override
    @Transactional
    public CalculateFeeResponse calculateParkingFee(CalculateFeeRequest request) {
        // 1. 根据入场记录ID查询入场记录
        ParkingRecord inRecord = parkingRecordMapper.selectById(request.getInRecordId());
        if (inRecord == null) {
            // 实际项目中建议抛业务异常，由全局异常处理器统一返回
            throw new IllegalArgumentException("入场记录不存在，id=" + request.getInRecordId());
        }

        // 2. 取出入场时间和出场时间
        LocalDateTime inTime = inRecord.getInTime();
        LocalDateTime outTime = request.getOutTime();
        if (outTime.isBefore(inTime)) {
            throw new IllegalArgumentException("出场时间不能早于入场时间");
        }

        // 3. 计算停车时长（分钟）
        long minutes = Duration.between(inTime, outTime).toMinutes();
        if (minutes == 0) {
            // 停车不足1分钟按1分钟算，避免0费用
            minutes = 1;
        }

        // 4. 按简单规则计算费用（示例：每小时5元，不足一小时按一小时）
        long hours = (minutes + 59) / 60; // 向上取整
        long amount = hours * 500;        // 5元 = 500分

        // 5. 生成收费订单并入库
        ChargeOrder order = new ChargeOrder();
        order.setInRecordId(inRecord.getId());
        order.setAmount(amount);
        order.setCreateTime(LocalDateTime.now());
        order.setOutTime(outTime);
        chargeOrderMapper.insert(order);

        // 6. 更新入场记录的出场时间和支付状态（示例）
        inRecord.setOutTime(outTime);
        inRecord.setPaidAmount(amount);
        inRecord.setStatus("FINISHED");
        parkingRecordMapper.updateById(inRecord);

        // 7. 组装响应对象返回给 Controller
        CalculateFeeResponse resp = new CalculateFeeResponse();
        resp.setInRecordId(inRecord.getId());
        resp.setParkingMinutes(minutes);
        resp.setAmount(amount);
        resp.setRuleName("简单按小时计费");
        resp.setRealOutTime(outTime);
        return resp;
    }
}