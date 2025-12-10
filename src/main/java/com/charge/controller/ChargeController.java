package com.charge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.charge.entity.DTO.CalculateFeeRequest;
import com.charge.entity.DTO.ConfirmPaymentRequest;
import com.charge.entity.VO.CalculateFeeResponse;
import com.charge.service.ChargeService;
import com.common.result.Result;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/charge")
public class ChargeController {

    public final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    /**
     * 出场计费：计算金额 + 写出场时间 + 生成未支付订单
     */
    @PostMapping("/calculate") 
    public Result<CalculateFeeResponse> calculateFee(@RequestBody @Valid CalculateFeeRequest request) {
        CalculateFeeResponse response = chargeService.calculateParkingFee(request);
        return Result.success(response);
    }

    /**
     * 支付成功确认：更新订单为已支付，并写入入场记录的支付状态
     */
    @PostMapping("/confirmPayment")
    public void confirmPayment(@RequestBody @Valid ConfirmPaymentRequest request) {
        chargeService.confirmPayment(request);
    }
}
