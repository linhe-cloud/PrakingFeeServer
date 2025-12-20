package com.charge.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.charge.entity.PaymentTransaction;

public interface PaymentTransactionMapper {

    int insert(PaymentTransaction tx);
    
    int updateStatus(PaymentTransaction tx);

    PaymentTransaction selectById(@Param("id") Long id);

    List<PaymentTransaction> selectByOrderId(@Param("orderId") Long orderId);

    PaymentTransaction selectByThirdTradeNo(@Param("thirdTradeNo") String thirdTradeNo);
}
