package com.charge.service;

import com.charge.entity.PaymentTransaction;

public interface PaymentService {

    /**
     * 创建一条支付流水（INIT）
     * 一般在发起支付时调用
     */
    PaymentTransaction createTransaction(Long orderId,
                                         Long userId,
                                         Long amount,
                                         String payChannel);

    /**
     * 标记流水支付成功，并同步更新订单为已支付
     * 幂等：如果流水或订单已是成功状态，直接返回
     */
    void markSuccess(Long transactionId, String thirdTradeNo);

    /**
     * 标记流水支付失败
     */
    void markFailed(Long transactionId, String remark);

    /**
     * 标记退款成功（先做记录，具体三方退款可后置）
     */
    void markRefund(Long transactionId, String remark);
}