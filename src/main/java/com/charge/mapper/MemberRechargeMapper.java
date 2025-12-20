package com.charge.mapper;

import com.charge.entity.MemberRecharge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberRechargeMapper {

    int insert(MemberRecharge recharge);

    int updatePayStatus(MemberRecharge recharge);

    MemberRecharge selectByRechargeNo(@Param("rechargeNo") String rechargeNo);

    MemberRecharge selectById(@Param("id") Long id);

    List<MemberRecharge> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户累计充值金额（只统计已支付的）
     */
    Long sumTotalRechargeByUserId(@Param("userId") Long userId);
}
