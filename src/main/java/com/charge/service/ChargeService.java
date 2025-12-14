package com.charge.service;

import com.charge.entity.DTO.CalculateFeeRequest;
import com.charge.entity.VO.CalculateFeeResponse;
import com.charge.entity.DTO.ConfirmPaymentRequest;


/**
 * 计费服务接口
 * 定义与“收费/计费”相关的业务能力
 */
public interface ChargeService {
    /**
     * 出场计费：计算金额 + 写出场时间 + 生成未支付订单
     */
    CalculateFeeResponse calculateParkingFee(CalculateFeeRequest request);

    /**
     * 支付成功确认：更新订单为以支付，并写入入场记录的支付状态
     */
    void confirmPayment(ConfirmPaymentRequest request);

    /**
     * 费用预览：只计算金额，不生成订单，不更新记录
     */
    CalculateFeeResponse previewParkingFee(CalculateFeeRequest request);
    
} 
