package com.charge.service;

import com.charge.entity.MemberRecharge;

import java.util.List;

public interface MemberRechargeService {

    /**
     * 创建充值订单（未支付）
     * @param userId 用户ID
     * @param amount 充值金额(分)
     * @return 充值单号
     */
    String createRechargeOrder(Long userId, Long amount);

    /**
     * 支付成功回调（核心：充值到账 + 自动开通/升级会员）
     * @param rechargeNo 充值单号
     * @param payChannel 支付渠道
     */
    void paySuccess(String rechargeNo, String payChannel);

    /**
     * 查询充值记录
     */
    MemberRecharge getByRechargeNo(String rechargeNo);

    /**
     * 查询用户充值记录列表
     */
    List<MemberRecharge> listByUserId(Long userId);
}
