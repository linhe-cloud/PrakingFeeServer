package com.charge.mapper;

import com.charge.entity.OrderDiscount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderDiscountMapper {

    int insert(OrderDiscount record);

    List<OrderDiscount> selectByOrderId(@Param("orderId") Long orderId);
}
