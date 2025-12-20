package com.charge.service.impl;

import com.charge.entity.ChargeOrder;
import com.charge.entity.PaymentTransaction;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.mapper.PaymentTransactionMapper;
import com.charge.service.PaymentService;
import com.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionMapper paymentTransactionMapper;
    private final ChargeOrderMapper chargeOrderMapper;

    public PaymentServiceImpl(PaymentTransactionMapper paymentTransactionMapper,
                              ChargeOrderMapper chargeOrderMapper) {
        this.paymentTransactionMapper = paymentTransactionMapper;
        this.chargeOrderMapper = chargeOrderMapper;
    }

    @Override
    @Transactional
    public PaymentTransaction createTransaction(Long orderId,
                                                Long userId,
                                                Long amount,
                                                String payChannel) {
        // 1. 参数基础校验
        if (orderId == null) {
            throw new BusinessException("订单ID不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }

        // 2. 查询并校验订单
        ChargeOrder order = chargeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 3. 创建交易记录（初始状态：0-INIT）
        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderId(orderId);
        tx.setOrderNo(order.getOrderNo());
        tx.setUserId(userId);         // 可以为 null，不强依赖 ChargeOrder 是否有 userId
        tx.setPayChannel(payChannel);
        tx.setAmount(amount);
        tx.setStatus(0);              // 0-INIT，后续 markSuccess/markFailed 再更新

        paymentTransactionMapper.insert(tx);
        return tx;
    }

        

    @Override
    @Transactional
    public void markSuccess(Long transactionId, String thirdTradeNo) {
        if (transactionId == null) {
            throw new BusinessException("支付流水ID不能为空");
        }
        PaymentTransaction tx = paymentTransactionMapper.selectById(transactionId);
        if (tx == null) {
            throw new BusinessException("支付流水不存在");
        }
        if (tx.getStatus() != null && tx.getStatus() == 1) {
            // 已经成功，幂等返回
            return;
        }

        // 更新流水状态
        tx.setStatus(1);
        tx.setThirdTradeNo(thirdTradeNo);
        paymentTransactionMapper.updateStatus(tx);

        // 更新订单状态为已支付
        ChargeOrder order = chargeOrderMapper.selectById(tx.getOrderId());
        if (order == null) {
            throw new BusinessException("关联订单不存在");
        }
        // 如果订单已是已支付/已退款，也不重复更改
        String payStatus = order.getPayStatus();
        if ("PAID".equals(payStatus) || "REFUNDED".equals(payStatus)) {
            return;
        }

        order.setPayStatus("PAID");   // 已支付
        order.setPayChannel(tx.getPayChannel());
        order.setPayTime(LocalDateTime.now());
        chargeOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void markFailed(Long transactionId, String remark) {
        if (transactionId == null) {
            throw new BusinessException("支付流水ID不能为空");
        }
        PaymentTransaction tx = paymentTransactionMapper.selectById(transactionId);
        if (tx == null) {
            throw new BusinessException("支付流水不存在");
        }
        // 已成功/已退款的不改为失败
        if (tx.getStatus() != null && (tx.getStatus() == 1 || tx.getStatus() == 3)) {
            return;
        }
        tx.setStatus(2); // FAILED
        tx.setRemark(remark);
        paymentTransactionMapper.updateStatus(tx);
    }

    @Override
    @Transactional
    public void markRefund(Long transactionId, String remark) {
        if (transactionId == null) {
            throw new BusinessException("支付流水ID不能为空");
        }
        PaymentTransaction tx = paymentTransactionMapper.selectById(transactionId);
        if (tx == null) {
            throw new BusinessException("支付流水不存在");
        }
        tx.setStatus(3); // REFUNDED
        tx.setRemark(remark);
        paymentTransactionMapper.updateStatus(tx);

        // 同步订单状态为已退款（简单处理）
        ChargeOrder order = chargeOrderMapper.selectById(tx.getOrderId());
        if (order != null) {
            order.setPayStatus("REFUNDED"); // 已退款
            chargeOrderMapper.updateById(order);
        }
    }

}
