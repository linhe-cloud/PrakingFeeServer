package com.charge.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.charge.entity.ChargeOrder;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChargeOrderMapper {

    int insert(ChargeOrder order);

    ChargeOrder selectById(@Param("id") Long id);

    int updateById(ChargeOrder order);
}
