package com.charge.service.impl;

import com.charge.entity.DTO.CalculateFeeRequest;
import com.charge.entity.DTO.ConfirmPaymentRequest;
import com.charge.entity.VO.CalculateFeeResponse;
import com.charge.entity.ParkingRecord;
import com.charge.entity.ChargeOrder;
import com.charge.entity.ChargeRule;
import com.charge.entity.ParkingIot;

import com.charge.service.ChargeRuleService;
import com.charge.service.ChargeService;

import com.charge.mapper.ParkingRecordMapper;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.mapper.ParkingIotMapper;

import com.common.exception.BusinessException;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 计费服务实现类
 * 核心业务逻辑都写在这里
 */
@Service
public class ChargeServiceImpl implements ChargeService {

    private final ParkingRecordMapper parkingRecordMapper;
    private final ParkingIotMapper parkingIotMapper;
    private final ChargeOrderMapper chargeOrderMapper;
    private final ChargeRuleService chargeRuleService;

    public ChargeServiceImpl(ParkingRecordMapper parkingRecordMapper,
                             ParkingIotMapper parkingIotMapper,
                             ChargeOrderMapper chargeOrderMapper,
                             ChargeRuleService chargeRuleService) {
        this.parkingRecordMapper = parkingRecordMapper;
        this.parkingIotMapper = parkingIotMapper;
        this.chargeOrderMapper = chargeOrderMapper;
        this.chargeRuleService = chargeRuleService;
    }

    /**
     * 生成业务订单号
     */
    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(0, 1000000);
        String randomPart = String.format("%06d", random);
        return "CO" + timePart + randomPart;
    }

    /**
     * 预览计费：计算金额，但不生成订单
     */
    @Override
    public CalculateFeeResponse previewParkingFee(CalculateFeeRequest request) {
        ParkingRecord inRecord = parkingRecordMapper.selectById(request.getInRecordId());
        if (inRecord == null) {
            throw new BusinessException("入场记录不存在，id=" + request.getInRecordId());
        }

        LocalDateTime inTime = inRecord.getInTime();
        LocalDateTime outTime = request.getExitTime();
        if (outTime.isBefore(inTime)) {
            throw new BusinessException("出场时间不能早于入场时间");
        }

        Long parkingIotId = inRecord.getParkingIotId();
        if (parkingIotId == null) {
            throw new BusinessException("停车记录未关联车场,id=" + inRecord.getId());
        }

        long minutes = Duration.between(inTime, outTime).toMinutes();
        if (minutes <= 0) {
            minutes = 1; // 至少1分钟
        }

        String feeRuleName;
        long amountInCents;

        ChargeRule rule = chargeRuleService.getApplicableRule(parkingIotId);
        if (rule != null) {
            feeRuleName = rule.getRuleName();
            amountInCents = chargeRuleService.calculateAmount(rule, minutes);
        } else {
            feeRuleName = "前30分钟免费,之后按小时计费"; // 默认规则名称

            ParkingIot parkingIot = parkingIotMapper.selectById(inRecord.getParkingIotId());
            Integer unitPrice = parkingIot.getUnitPrice(); // 假设是 500 表示 5.00 元/小时
            if (unitPrice == null || unitPrice <= 0) {
                throw new BusinessException("车场未配置有效单价");
            } 
            // 实现“前30分钟免费”的计费规则
            if (minutes <= 30) {
                amountInCents = 0; // 免费
            } else {
                // 超过30分钟：从第31分钟开始计费，按小时向上取整
                long chargeableMinutes = minutes - 30;          // 扣除免费30分钟
                long hours = (chargeableMinutes + 59) / 60;     // 向上取整
                amountInCents = hours * unitPrice;              // 计算金额（单位：分）
                // 如果正好30分01秒，也会变成1小时
            }
        }

        // 组装响应，仅返回计算结果，不生成订单
        CalculateFeeResponse resp = new CalculateFeeResponse();
        resp.setInRecordId(inRecord.getId());
        resp.setParkingMinutes(minutes);
        resp.setAmount(amountInCents);
        resp.setFeeRuleName(feeRuleName);
        resp.setRealOutTime(outTime);
        return resp;        
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
            throw new BusinessException("入场记录不存在，id=" + request.getInRecordId());
        }

        // 2. 取出入场时间和出场时间
        LocalDateTime inTime = inRecord.getInTime();
        LocalDateTime outTime = request.getExitTime();
        if (outTime.isBefore(inTime)) {
            throw new BusinessException("出场时间不能早于入场时间");
        }

        // 3. 计算停车时长(分钟)
        long minutes = Duration.between(inTime, outTime).toMinutes();
        if (minutes <= 0) {
            minutes = 1; // 至少1分钟
        }

        Long parkingIotId = inRecord.getParkingIotId();
        if (parkingIotId == null) {
            throw new BusinessException("停车记录未关联车场,id=" + inRecord.getId());
        }

        // 4. 提前计算计费规则名称,用于幂等性检查和正常流程
        String feeRuleName;
        ChargeRule rule = chargeRuleService.getApplicableRule(parkingIotId);
        if (rule != null) {
            feeRuleName = rule.getRuleName();
        } else {
            feeRuleName = "前30分钟免费,之后按小时计费"; // 默认规则名称
        }

        // ========= 幂等性检查:同一入场记录 + 同一出场时间,直接返回已有订单 ========
        ChargeOrder exisOrder = chargeOrderMapper.selectByInRecordIdAndOutTime(inRecord.getId(), outTime);
        if (exisOrder != null) {
            // 已有订单,直接组装响应返回
            CalculateFeeResponse resp = new CalculateFeeResponse();
            resp.setInRecordId(inRecord.getId());
            resp.setOrderId(exisOrder.getId());
            resp.setParkingMinutes(minutes);
            resp.setAmount(exisOrder.getAmount());
            resp.setFeeRuleName(feeRuleName);
            resp.setRealOutTime(outTime);
            return resp;
        }
        // ========= 幂等性检查结束 =========


        //=========第一次消费逻辑=========

        long amountInCents;     // 计算金额(单位:分)

        // 5. 根据计费规则计算金额
        if (rule != null) {
            amountInCents = chargeRuleService.calculateAmount(rule, minutes);
        } else {
            ParkingIot parkingIot = parkingIotMapper.selectById(inRecord.getParkingIotId());
            Integer unitPrice = parkingIot.getUnitPrice(); // 假设是 500 表示 5.00 元/小时
            if (unitPrice == null || unitPrice <= 0) {
                throw new BusinessException("车场未配置有效单价");
            }
            // 实现“前30分钟免费”的计费规则
            if (minutes <= 30) {
                amountInCents = 0; // 免费
            } else {
                // 超过30分钟：从第31分钟开始计费，按小时向上取整
                long chargeableMinutes = minutes - 30;          // 扣除免费30分钟
                long hours = (chargeableMinutes + 59) / 60;     // 向上取整
                amountInCents = hours * unitPrice;              // 计算金额（单位：分）
                // 如果正好30分01秒，也会变成1小时
            }
        }

        // 5. 生成收费订单并入库
        ChargeOrder order = new ChargeOrder();
        order.setOrderNo(generateOrderNo());
        order.setInRecordId(inRecord.getId());
        order.setAmount(amountInCents);
        order.setPayStatus("UNPAID"); // 未支付
        order.setPayChannel(null);
        order.setPayTime(null);
        order.setOutTime(outTime);
        order.setFeeRuleName(feeRuleName);
        order.setCreateTime(LocalDateTime.now());
        chargeOrderMapper.insert(order);

        // 6. 更新入场记录的出场时间
        inRecord.setOutTime(outTime);
        parkingRecordMapper.updateById(inRecord);

        // 7. 组装响应对象返回给 Controller
        CalculateFeeResponse resp = new CalculateFeeResponse();
        resp.setInRecordId(inRecord.getId());
        resp.setOrderId(order.getId());
        resp.setParkingMinutes(minutes);
        resp.setAmount(amountInCents);
        resp.setFeeRuleName(feeRuleName);
        resp.setRealOutTime(outTime);
        return resp;
    }

    /**
     * 支付成功确认：更新订单为已支付，并写入入场记录的支付状态
     */
    @Override
    @Transactional
    public void confirmPayment(ConfirmPaymentRequest request) {
        // 1. 根据订单ID查询订单
        ChargeOrder order = chargeOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在，id=" + request.getOrderId());
        }

        if (!"UNPAID".equals(order.getPayStatus())) {
            throw new BusinessException("订单状态不允许重复支付");
        }

        // 2. 更新订单为已支付（暂时写死渠道为 CASH）
        order.setPayStatus("PAID");
        order.setPayChannel("CASH");
        order.setPayTime(LocalDateTime.now());
        chargeOrderMapper.updateById(order);

        // 3. 更新入场记录的支付状态
        ParkingRecord inRecord = parkingRecordMapper.selectById(order.getInRecordId());
        if (inRecord == null) {
            throw new BusinessException("入场记录不存在，id=" + order.getInRecordId());
        }

        inRecord.setPaidAmount(order.getAmount());
        inRecord.setStatus("FINISHED");
        parkingRecordMapper.updateById(inRecord);
    }

}