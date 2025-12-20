package com.charge.controller.admin;

import com.charge.entity.PaymentTransaction;
import com.charge.mapper.PaymentTransactionMapper;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付流水管理接口（管理端）
 */
@RestController
@RequestMapping("/api/admin/payment")
public class PaymentAdminController {

    private final PaymentTransactionMapper paymentTransactionMapper;

    public PaymentAdminController(PaymentTransactionMapper paymentTransactionMapper) {
        this.paymentTransactionMapper = paymentTransactionMapper;
    }

    /**
     * 按订单ID查询支付流水列表
     */
    @GetMapping("/list/order/{orderId}")
    public Result<List<PaymentTransaction>> listByOrderId(@PathVariable("orderId") Long orderId) {
        List<PaymentTransaction> list = paymentTransactionMapper.selectByOrderId(orderId);
        return Result.success(list);
    }

    /**
     * 按流水ID查询详情
     */
    @GetMapping("/detail/{id}")
    public Result<PaymentTransaction> getDetail(@PathVariable("id") Long id) {
        PaymentTransaction tx = paymentTransactionMapper.selectById(id);
        return Result.success(tx);
    }

    /**
     * 按第三方交易号查询
     */
    @GetMapping("/query/thirdTradeNo")
    public Result<PaymentTransaction> getByThirdTradeNo(@RequestParam("thirdTradeNo") String thirdTradeNo) {
        PaymentTransaction tx = paymentTransactionMapper.selectByThirdTradeNo(thirdTradeNo);
        return Result.success(tx);
    }
}
