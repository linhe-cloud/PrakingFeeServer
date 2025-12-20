package com.charge.service.impl;

import com.charge.entity.Member;
import com.charge.entity.MemberRecharge;
import com.charge.mapper.MemberRechargeMapper;
import com.charge.service.MemberRechargeService;
import com.charge.service.MemberService;
import com.charge.service.WalletService;
import com.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class MemberRechargeServiceImpl implements MemberRechargeService {

    private final MemberRechargeMapper rechargeMapper;
    private final WalletService walletService;
    private final MemberService memberService;

    public MemberRechargeServiceImpl(MemberRechargeMapper rechargeMapper,
                                     WalletService walletService,
                                     MemberService memberService) {
        this.rechargeMapper = rechargeMapper;
        this.walletService = walletService;
        this.memberService = memberService;
    }

    @Override
    @Transactional
    public String createRechargeOrder(Long userId, Long amount) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (amount == null || amount <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        // 生成充值单号
        String rechargeNo = generateRechargeNo();

        // 计算赠送金额（充值优惠规则）
        long bonusAmount = calculateBonus(amount);
        long totalAmount = amount + bonusAmount;

        // 创建充值记录
        MemberRecharge recharge = new MemberRecharge();
        recharge.setUserId(userId);
        recharge.setRechargeNo(rechargeNo);
        recharge.setAmount(amount);
        recharge.setBonusAmount(bonusAmount);
        recharge.setTotalAmount(totalAmount);
        recharge.setPayStatus(0); // 待支付
        recharge.setMemberUpgraded(0);

        rechargeMapper.insert(recharge);
        return rechargeNo;
    }

    @Override
    @Transactional
    public void paySuccess(String rechargeNo, String payChannel) {
        if (rechargeNo == null || rechargeNo.isEmpty()) {
            throw new BusinessException("充值单号不能为空");
        }

        // 查询充值记录
        MemberRecharge recharge = rechargeMapper.selectByRechargeNo(rechargeNo);
        if (recharge == null) {
            throw new BusinessException("充值记录不存在");
        }
        if (recharge.getPayStatus() == 1) {
            // 幂等性：已经支付过了，直接返回
            return;
        }

        // 1. 更新充值记录状态
        recharge.setPayStatus(1);
        recharge.setPayChannel(payChannel);
        recharge.setPayTime(LocalDateTime.now());

        // 2. 充值到账（增加钱包余额）
        walletService.recharge(recharge.getUserId(), recharge.getTotalAmount());

        // 3. 【核心】判断是否需要自动开通/升级会员
        boolean upgraded = autoUpgradeMember(recharge.getUserId());
        recharge.setMemberUpgraded(upgraded ? 1 : 0);

        // 4. 更新充值记录
        rechargeMapper.updatePayStatus(recharge);
    }

    @Override
    public MemberRecharge getByRechargeNo(String rechargeNo) {
        if (rechargeNo == null || rechargeNo.isEmpty()) {
            throw new BusinessException("充值单号不能为空");
        }
        return rechargeMapper.selectByRechargeNo(rechargeNo);
    }

    @Override
    public List<MemberRecharge> listByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return rechargeMapper.selectByUserId(userId);
    }

    // ======================== 私有方法 ========================

    /**
     * 生成充值单号：RC + yyyyMMddHHmmss + 6位随机数
     */
    private String generateRechargeNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = new Random().nextInt(900000) + 100000;
        return "RC" + timestamp + randomNum;
    }

    /**
     * 计算充值赠送金额
     * 规则示例：
     * - 充值 >= 10000分(100元)，赠送 10%
     * - 充值 >= 50000分(500元)，赠送 15%
     * - 充值 >= 100000分(1000元)，赠送 20%
     */
    private long calculateBonus(long amount) {
        if (amount >= 100000) { // >= 1000元
            return (long) (amount * 0.20);
        } else if (amount >= 50000) { // >= 500元
            return (long) (amount * 0.15);
        } else if (amount >= 10000) { // >= 100元
            return (long) (amount * 0.10);
        }
        return 0L;
    }

    /**
     * 【核心方法】自动开通/升级会员
     * 
     * 会员等级规则（基于累计充值金额）：
     * - level=1（普通会员）：累计充值 >= 10000分(100元)，9折优惠
     * - level=2（VIP会员）：累计充值 >= 50000分(500元)，8折优惠
     * - level=3（黑金会员）：累计充值 >= 100000分(1000元)，免费停车
     * 
     * @return true-发生了会员升级，false-未升级
     */
    private boolean autoUpgradeMember(Long userId) {
        // 查询用户累计充值金额
        Long totalRecharge = rechargeMapper.sumTotalRechargeByUserId(userId);
        if (totalRecharge == null) {
            totalRecharge = 0L;
        }

        // 查询当前会员信息
        Member existingMember = memberService.getByUserId(userId);

        // 判断应该开通的会员等级
        Integer targetLevel = null;
        BigDecimal targetDiscountRate = null;
        Integer targetFreeParking = 0;

        if (totalRecharge >= 100000) {
            // 黑金会员：免费停车
            targetLevel = 3;
            targetDiscountRate = BigDecimal.ONE;
            targetFreeParking = 1;
        } else if (totalRecharge >= 50000) {
            // VIP会员：8折
            targetLevel = 2;
            targetDiscountRate = new BigDecimal("0.80");
            targetFreeParking = 0;
        } else if (totalRecharge >= 10000) {
            // 普通会员：9折
            targetLevel = 1;
            targetDiscountRate = new BigDecimal("0.90");
            targetFreeParking = 0;
        }

        // 如果未达到开通门槛
        if (targetLevel == null) {
            return false;
        }

        // 判断是否需要新建或升级
        if (existingMember == null) {
            // 新建会员
            Member newMember = new Member();
            newMember.setUserId(userId);
            newMember.setLevel(targetLevel);
            newMember.setDiscountRate(targetDiscountRate);
            newMember.setFreeParking(targetFreeParking);
            newMember.setStatus(1);
            newMember.setRemark("充值自动开通");
            memberService.createMember(newMember);
            return true;
        } else {
            // 如果当前等级低于目标等级，则升级
            if (existingMember.getLevel() == null || existingMember.getLevel() < targetLevel) {
                existingMember.setLevel(targetLevel);
                existingMember.setDiscountRate(targetDiscountRate);
                existingMember.setFreeParking(targetFreeParking);
                existingMember.setStatus(1);
                existingMember.setRemark("充值自动升级");
                memberService.updateMember(existingMember);
                return true;
            }
        }

        return false;
    }
}
