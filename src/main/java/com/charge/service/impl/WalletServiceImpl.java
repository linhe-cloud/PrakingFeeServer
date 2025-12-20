package com.charge.service.impl;

import com.charge.entity.Wallet;
import com.charge.mapper.WalletMapper;
import com.charge.service.WalletService;
import com.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    @Override
    public Wallet getOrCreateWallet(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            // 自动创建钱包
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance(0L);
            wallet.setTotalRecharge(0L);
            wallet.setTotalConsume(0L);
            wallet.setStatus(1);
            walletMapper.insert(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet getBalance(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null) {
            throw new BusinessException("用户钱包不存在");
        }
        return wallet;
    }

    @Override
    @Transactional
    public void recharge(Long userId, Long amount) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        
        // 确保钱包存在
        getOrCreateWallet(userId);
        
        // 增加余额
        int rows = walletMapper.addBalance(userId, amount);
        if (rows == 0) {
            throw new BusinessException("充值失败");
        }
    }

    @Override
    @Transactional
    public boolean deduct(Long userId, Long amount) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BusinessException("扣款金额必须大于0");
        }
        
        int rows = walletMapper.deductBalance(userId, amount);
        return rows > 0; // 返回 false 表示余额不足
    }
}
