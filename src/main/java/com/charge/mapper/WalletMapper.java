package com.charge.mapper;

import com.charge.entity.Wallet;
import org.apache.ibatis.annotations.Param;

public interface WalletMapper {

    int insert(Wallet wallet);

    Wallet selectByUserId(@Param("userId") Long userId);

    Wallet selectById(@Param("id") Long id);

    /**
     * 增加余额和累计充值
     */
    int addBalance(@Param("userId") Long userId, 
                   @Param("amount") Long amount);

    /**
     * 扣减余额并增加累计消费
     */
    int deductBalance(@Param("userId") Long userId, 
                      @Param("amount") Long amount);
}
