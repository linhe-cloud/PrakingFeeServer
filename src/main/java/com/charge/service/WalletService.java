package com.charge.service;

import com.charge.entity.Wallet;

public interface WalletService {

    /**
     * 获取或创建用户钱包
     */
    Wallet getOrCreateWallet(Long userId);

    /**
     * 查询余额
     */
    Wallet getBalance(Long userId);

    /**
     * 充值（由充值流程调用）
     */
    void recharge(Long userId, Long amount);

    /**
     * 扣款（由停车计费调用）
     */
    boolean deduct(Long userId, Long amount);
}
