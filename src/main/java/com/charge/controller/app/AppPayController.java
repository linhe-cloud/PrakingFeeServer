package com.charge.controller.app;

import com.charge.entity.PaymentTransaction;
import com.charge.entity.Wallet;
import com.charge.entity.ChargeOrder;
import com.charge.service.PaymentService;
import com.charge.service.WalletService;
import com.charge.mapper.ChargeOrderMapper;
import com.common.exception.BusinessException;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付接口（APP端）
 */
@RestController
@RequestMapping("/api/app/pay")
public class AppPayController {

    private final PaymentService paymentService;
    private final WalletService walletService;
    private final ChargeOrderMapper chargeOrderMapper;

    public AppPayController(PaymentService paymentService,
                            WalletService walletService,
                            ChargeOrderMapper chargeOrderMapper) {
        this.paymentService = paymentService;
        this.walletService = walletService;
        this.chargeOrderMapper = chargeOrderMapper;
    }

    /**
     * 创建支付流水（用于扫码支付等场景）
     * payChannel: WECHAT / ALIPAY / CASH / ETC / WALLET
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayment(
            @RequestParam("orderId") Long orderId,
            @RequestParam("userId") Long userId,
            @RequestParam("payChannel") String payChannel) {

        ChargeOrder order = chargeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getAmount() == null || order.getAmount() <= 0) {
            throw new BusinessException("订单金额非法");
        }

        PaymentTransaction tx = paymentService.createTransaction(
                orderId,
                userId,
                order.getAmount(),
                payChannel
        );

        Map<String, Object> data = new HashMap<>();
        data.put("transactionId", tx.getId());
        data.put("orderId", orderId);
        data.put("orderNo", order.getOrderNo());
        data.put("amount", order.getAmount());
        data.put("payChannel", payChannel);
        data.put("message", "支付流水创建成功");

        // 对于扫码支付，这里可以返回用于生成二维码的参数（后续扩展）
        return Result.success(data);
    }

    /**
     * 钱包余额支付（直接扣钱包+更新订单+记录流水）
     */
    @PostMapping("/walletPay")
    public Result<Void> walletPay(
            @RequestParam("orderId") Long orderId,
            @RequestParam("userId") Long userId) {

        ChargeOrder order = chargeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getAmount() == null || order.getAmount() <= 0) {
            throw new BusinessException("订单金额非法");
        }

        // 先检查钱包余额
        Wallet wallet = walletService.getBalance(userId);
        if (wallet.getBalance() == null || wallet.getBalance() < order.getAmount()) {
            throw new BusinessException("钱包余额不足");
        }

        // 扣减钱包
        boolean ok = walletService.deduct(userId, order.getAmount());
        if (!ok) {
            throw new BusinessException("扣款失败，余额不足");
        }

        // 创建流水并标记成功
        PaymentTransaction tx = paymentService.createTransaction(
                orderId,
                userId,
                order.getAmount(),
                "WALLET"
        );
        paymentService.markSuccess(tx.getId(), null);

        return Result.success();
    }

    /**
     * 模拟三方支付回调（测试用）
     * 实际上线时可改为微信/支付宝回调地址
     */
    @PostMapping("/callback/mockSuccess")
    public Result<Void> mockPaySuccess(
            @RequestParam("transactionId") Long transactionId,
            @RequestParam(value = "thirdTradeNo", required = false) String thirdTradeNo) {

        paymentService.markSuccess(transactionId, thirdTradeNo);
        return Result.success();
    }
}