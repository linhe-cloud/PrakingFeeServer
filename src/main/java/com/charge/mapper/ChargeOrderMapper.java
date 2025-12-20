package com.charge.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import com.charge.entity.ChargeOrder;

import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface ChargeOrderMapper {

    int insert(ChargeOrder order);

    ChargeOrder selectById(@Param("id") Long id);

    int updateById(ChargeOrder order);

    // 幂等性用：根据入场ID和出场时间查询订单
    ChargeOrder selectByInRecordIdAndOutTime(@Param("inRecordId") Long inRecordId, 
                                             @Param("outTime") LocalDateTime outTime);

    /**
     * 分页查询订单列表（按支付状态筛选）
     */
    List<ChargeOrder> selectPage(@Param("payStatus") String payStatus,
                                 @Param("offset") Long offset,
                                 @Param("limit") Long limit);

    /**
     * 统计订单总数（按支付状态筛选）
     */
    Long countByCondition(@Param("payStatus") String payStatus);

    /**
     * 根据入场记录ID查询订单（小程序端：从停车记录关联订单）
     */
    ChargeOrder selectByInRecordId(@Param("inRecordId") Long inRecordId);
}
