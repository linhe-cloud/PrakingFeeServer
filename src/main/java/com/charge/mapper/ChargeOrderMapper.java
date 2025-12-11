package com.charge.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.charge.entity.ChargeOrder;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ChargeOrderMapper {

    int insert(ChargeOrder order);

    ChargeOrder selectById(@Param("id") Long id);

    int updateById(ChargeOrder order);

    // 幂等性用：根据入场ID和出场时间查询订单
    ChargeOrder selectByInRecordIdAndOutTime(@Param("inRecordId") Long inRecordId, 
                                             @Param("outTime") LocalDateTime outTime);
}
