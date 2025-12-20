package com.charge.mapper;

import com.charge.entity.OrderModificationRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderModificationRecordMapper {

    int insert(OrderModificationRecord record);

    int updateStatus(OrderModificationRecord record);

    OrderModificationRecord selectById(@Param("id") Long id);

    List<OrderModificationRecord> selectByOrderId(@Param("orderId") Long orderId);

    List<OrderModificationRecord> selectPendingList();
}
