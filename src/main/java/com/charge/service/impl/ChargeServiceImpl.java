package com.charge.service.impl;

import com.charge.entity.DTO.CalculateFeeRequest;
import com.charge.entity.DTO.ConfirmPaymentRequest;
import com.charge.entity.VO.CalculateFeeResponse;
import com.charge.entity.ChargeOrder;
import com.charge.entity.ChargeRule;
import com.charge.entity.Member;
import com.charge.entity.DiscountRule;
import com.charge.entity.OrderDiscount;
import com.parking.entity.ParkingRecord;
import com.parking.entity.ParkingIot;

import com.charge.service.ChargeRuleService;
import com.charge.service.ChargeService;
import com.charge.service.DiscountRuleService;
import com.charge.service.ChargeCacheService;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.mapper.MemberMapper;
import com.charge.mapper.OrderDiscountMapper;
import com.parking.mapper.ParkingRecordMapper;
import com.parking.mapper.ParkingIotMapper;

import com.common.exception.BusinessException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

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
    private final DiscountRuleService discountRuleService;
    private final MemberMapper memberMapper;
    private final OrderDiscountMapper orderDiscountMapper;
    private final ChargeCacheService chargeCacheService;

    public ChargeServiceImpl(ParkingRecordMapper parkingRecordMapper,
                             ParkingIotMapper parkingIotMapper,
                             ChargeOrderMapper chargeOrderMapper,
                             ChargeRuleService chargeRuleService,
                             DiscountRuleService discountRuleService,
                             MemberMapper memberMapper,
                             OrderDiscountMapper orderDiscountMapper,
                             ChargeCacheService chargeCacheService) {
        this.parkingRecordMapper = parkingRecordMapper;
        this.parkingIotMapper = parkingIotMapper;
        this.chargeOrderMapper = chargeOrderMapper;
        this.chargeRuleService = chargeRuleService;
        this.discountRuleService = discountRuleService;
        this.memberMapper = memberMapper;
        this.orderDiscountMapper = orderDiscountMapper;
        this.chargeCacheService = chargeCacheService;
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
     * 内部优惠计算结果
     */
    private static class DiscountCalcResult {
        long originalAmount;
        long totalDiscountAmount;
        long payableAmount;
        List<OrderDiscount> discountRecords;

        DiscountCalcResult(long originalAmount) {
            this.originalAmount = originalAmount;
            this.totalDiscountAmount = 0L;
            this.payableAmount = originalAmount;
            this.discountRecords = new ArrayList<>();
        }
    }

    /**
     * 计算优惠（版本2：会员在前，规则在后，可叠加）
     *
     * @param originalAmount  原价金额（分）
     * @param userId          用户ID（会员用）
     * @param discountRuleCode 优惠规则编码（discount_rule.rule_code）
     */
    private DiscountCalcResult calculateDiscounts(long originalAmount, Long userId, String discountRuleCode) {
        DiscountCalcResult result = new DiscountCalcResult(originalAmount);

        if (originalAmount <= 0) {
            return result;
        }

        long base = originalAmount;

        // ============ Step1: 会员优惠 ============
        if (userId != null) {
            // 先从缓存获取会员信息
            Member member = chargeCacheService.getMember(userId);
            if (member == null) {
                // 缓存未命中，查询数据库
                member = memberMapper.selectByUserId(userId);
                // 写入缓存
                chargeCacheService.cacheMember(userId, member);
            } else if (member.getId() == null) {
                // 空对象标记表示非会员
                member = null;
            }
            
            if (member != null && member.getStatus() != null && member.getStatus() == 1) {
                Integer freeParking = member.getFreeParking();
                if (freeParking != null && freeParking == 1) {
                    // 免费停车
                    long memberDiscount = base;
                    if (memberDiscount > 0) {
                        OrderDiscount od = new OrderDiscount();
                        od.setSourceType("MEMBER");
                        od.setRuleCode(null);
                        od.setRuleName("会员免费停车");
                        od.setDiscountType("FREE");
                        od.setDiscountValue(100);
                        od.setDiscountAmount(memberDiscount);
                        result.discountRecords.add(od);

                        result.totalDiscountAmount += memberDiscount;
                        base -= memberDiscount;
                    }
                } else if (member.getDiscountRate() != null && member.getDiscountRate().compareTo(BigDecimal.ONE) < 0) {
                    // 折扣
                    BigDecimal rate = member.getDiscountRate();
                    BigDecimal baseDec = BigDecimal.valueOf(base);
                    long memberPay = baseDec.multiply(rate)
                            .setScale(0, RoundingMode.DOWN)
                            .longValue();
                    if (memberPay < 0) {
                        memberPay = 0;
                    }
                    long memberDiscount = base - memberPay;
                    if (memberDiscount > 0) {
                        OrderDiscount od = new OrderDiscount();
                        od.setSourceType("MEMBER");
                        od.setRuleCode(null);
                        od.setRuleName("会员折扣");
                        od.setDiscountType("PERCENT");
                        od.setDiscountValue(rate.multiply(BigDecimal.valueOf(100))
                                .setScale(0, RoundingMode.DOWN)
                                .intValue());
                        od.setDiscountAmount(memberDiscount);
                        result.discountRecords.add(od);

                        result.totalDiscountAmount += memberDiscount;
                        base = memberPay;
                    }
                }
            }
        }

        // 基数为0，不再继续其它优惠
        if (base <= 0) {
            result.payableAmount = 0;
            return result;
        }

        // ============ Step2: 规则 / 券 优惠（discount_rule） ============
        if (discountRuleCode != null && !discountRuleCode.isEmpty()) {
            // 先从缓存获取优惠规则
            DiscountRule rule = chargeCacheService.getDiscountRule(discountRuleCode);
            if (rule == null) {
                // 缓存未命中，查询数据库
                rule = discountRuleService.getEffectiveRuleByCode(discountRuleCode);
                // 写入缓存
                chargeCacheService.cacheDiscountRule(discountRuleCode, rule);
            } else if (rule.getId() == null) {
                // 空对象标记表示无此规则
                rule = null;
            }
            
            if (rule != null) {
                String type = rule.getDiscountType();
                Integer value = rule.getDiscountValue();
                Integer max = rule.getMaxDiscount();

                long ruleDiscount = 0L;

                if ("PERCENT".equalsIgnoreCase(type)) {
                    if (value != null && value > 0 && value < 100) {
                        BigDecimal percent = BigDecimal.valueOf(value)
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        long pay = BigDecimal.valueOf(base)
                                .multiply(percent)
                                .setScale(0, RoundingMode.DOWN)
                                .longValue();
                        if (pay < 0) {
                            pay = 0;
                        }
                        ruleDiscount = base - pay;
                    }
                } else if ("FIXED".equalsIgnoreCase(type)) {
                    if (value != null && value > 0) {
                        ruleDiscount = value.longValue();
                    }
                }

                if (ruleDiscount > base) {
                    ruleDiscount = base;
                }
                if (max != null && max > 0 && ruleDiscount > max.longValue()) {
                    ruleDiscount = max.longValue();
                }

                if (ruleDiscount > 0) {
                    OrderDiscount od = new OrderDiscount();
                    od.setSourceType("RULE");
                    od.setRuleCode(rule.getRuleCode());
                    od.setRuleName(rule.getRuleName());
                    od.setDiscountType(type);
                    od.setDiscountValue(value);
                    od.setDiscountAmount(ruleDiscount);
                    result.discountRecords.add(od);

                    result.totalDiscountAmount += ruleDiscount;
                    base -= ruleDiscount;
                }
            }
        }

        if (base < 0) {
            base = 0;
        }
        result.payableAmount = base;
        return result;
    }

    /**
     * 预览计费：计算金额，但不生成订单
     */
    @Override
    public CalculateFeeResponse previewParkingFee(CalculateFeeRequest request) {
        // 尝试从缓存获取预览结果
        CalculateFeeResponse cached = chargeCacheService.getFeePreview(
            request.getInRecordId(), 
            request.getExitTime()
        );
        if (cached != null) {
            return cached;
        }
        
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
            minutes = 1;
        }

        String feeRuleName;
        long originalAmountInCents;

        ChargeRule rule = chargeRuleService.getApplicableRule(parkingIotId);
        if (rule != null) {
            feeRuleName = rule.getRuleName();
            originalAmountInCents = chargeRuleService.calculateAmount(rule, minutes);
        } else {
            feeRuleName = "前30分钟免费,之后按小时计费";

            ParkingIot parkingIot = parkingIotMapper.selectById(inRecord.getParkingIotId());
            Integer unitPrice = parkingIot.getUnitPrice();
            if (unitPrice == null || unitPrice <= 0) {
                throw new BusinessException("车场未配置有效单价");
            }
            // 缓存车场信息
            chargeCacheService.cacheParkingIot(parkingIot);
            
            if (minutes <= 30) {
                originalAmountInCents = 0;
            } else {
                long chargeableMinutes = minutes - 30;
                long hours = (chargeableMinutes + 59) / 60;
                originalAmountInCents = hours * unitPrice;
            }
        }

        // 统一计算优惠（会员 + 规则），但不落库
        DiscountCalcResult discountResult = calculateDiscounts(
                originalAmountInCents,
                request.getUserId(),
                request.getDiscountRuleCode()
        );

        CalculateFeeResponse resp = new CalculateFeeResponse();
        resp.setInRecordId(inRecord.getId());
        resp.setParkingMinutes(minutes);
        resp.setAmount(discountResult.payableAmount);
        resp.setOriginalAmount(discountResult.originalAmount);
        resp.setDiscountAmount(discountResult.totalDiscountAmount);
        resp.setPayableAmount(discountResult.payableAmount);
        resp.setFeeRuleName(feeRuleName);
        resp.setRealOutTime(outTime);
        
        // 缓存预览结果（5分钟）
        chargeCacheService.cacheFeePreview(request.getInRecordId(), outTime, resp);
        
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
            minutes = 1;
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
            feeRuleName = "前30分钟免费,之后按小时计费";
        }

        // ========= 幂等性检查:同一入场记录 + 同一出场时间,直接返回已有订单 ========
        ChargeOrder exisOrder = chargeOrderMapper.selectByInRecordIdAndOutTime(inRecord.getId(), outTime);
        if (exisOrder != null) {
            // 已有订单，需从 order_discount 还原原价/优惠
            List<OrderDiscount> discounts = orderDiscountMapper.selectByOrderId(exisOrder.getId());
            long totalDiscount = 0L;
            if (discounts != null) {
                for (OrderDiscount od : discounts) {
                    if (od.getDiscountAmount() != null) {
                        totalDiscount += od.getDiscountAmount();
                    }
                }
            }
            long originalAmount = exisOrder.getAmount() + totalDiscount;

            CalculateFeeResponse resp = new CalculateFeeResponse();
            resp.setInRecordId(inRecord.getId());
            resp.setOrderId(exisOrder.getId());
            resp.setParkingMinutes(minutes);
            resp.setAmount(exisOrder.getAmount());
            resp.setOriginalAmount(originalAmount);
            resp.setDiscountAmount(totalDiscount);
            resp.setPayableAmount(exisOrder.getAmount());
            resp.setFeeRuleName(feeRuleName);
            resp.setRealOutTime(outTime);
            return resp;
        }
        // ========= 幂等性检查结束 =========

        //=========第一次计费逻辑=========

        long originalAmountInCents;

        // 5. 根据计费规则计算原价金额
        if (rule != null) {
            originalAmountInCents = chargeRuleService.calculateAmount(rule, minutes);
        } else {
            ParkingIot parkingIot = parkingIotMapper.selectById(inRecord.getParkingIotId());
            Integer unitPrice = parkingIot.getUnitPrice();
            if (unitPrice == null || unitPrice <= 0) {
                throw new BusinessException("车场未配置有效单价");
            }
            if (minutes <= 30) {
                originalAmountInCents = 0;
            } else {
                long chargeableMinutes = minutes - 30;
                long hours = (chargeableMinutes + 59) / 60;
                originalAmountInCents = hours * unitPrice;
            }
        }

        // 6. 计算优惠（会员 + 规则），并准备 order_discount 记录
        DiscountCalcResult discountResult = calculateDiscounts(
                originalAmountInCents,
                request.getUserId(),
                request.getDiscountRuleCode()
        );

        // 7. 生成收费订单并入库（amount = 应付金额）
        ChargeOrder order = new ChargeOrder();
        order.setOrderNo(generateOrderNo());
        order.setInRecordId(inRecord.getId());
        order.setAmount(discountResult.payableAmount);
        order.setPayStatus("UNPAID");
        order.setPayChannel(null);
        order.setPayTime(null);
        order.setOutTime(outTime);
        order.setFeeRuleName(feeRuleName);
        order.setCreateTime(LocalDateTime.now());
        chargeOrderMapper.insert(order);

        // 8. 落 order_discount 明细
        if (discountResult.discountRecords != null && !discountResult.discountRecords.isEmpty()) {
            for (OrderDiscount od : discountResult.discountRecords) {
                od.setOrderId(order.getId());
                orderDiscountMapper.insert(od);
            }
        }

        // 9. 更新入场记录的出场时间
        inRecord.setOutTime(outTime);
        parkingRecordMapper.updateById(inRecord);

        // 10. 组装响应对象
        CalculateFeeResponse resp = new CalculateFeeResponse();
        resp.setInRecordId(inRecord.getId());
        resp.setOrderId(order.getId());
        resp.setParkingMinutes(minutes);
        resp.setAmount(discountResult.payableAmount);
        resp.setOriginalAmount(discountResult.originalAmount);
        resp.setDiscountAmount(discountResult.totalDiscountAmount);
        resp.setPayableAmount(discountResult.payableAmount);
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
