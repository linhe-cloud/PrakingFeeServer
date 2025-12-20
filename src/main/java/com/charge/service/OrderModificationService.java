package com.charge.service;

import com.charge.entity.OrderModificationRecord;

import java.util.List;

public interface OrderModificationService {

    /**
     * 申请订单改价（需审批）
     */
    OrderModificationRecord applyAmountAdjust(Long orderId,
                                              Long afterAmount,
                                              String reason,
                                              Long applyUserId,
                                              String applyUserName);

    /**
     * 申请订单退款（需审批）
     */
    OrderModificationRecord applyRefund(Long orderId,
                                        String reason,
                                        Long applyUserId,
                                        String applyUserName);

    /**
     * 审批通过（主管操作）
     */
    void approve(Long recordId,
                 Long approveUserId,
                 String approveUserName,
                 String approveRemark);

    /**
     * 审批拒绝
     */
    void reject(Long recordId,
                Long approveUserId,
                String approveUserName,
                String approveRemark);

    /**
     * 查询待审批列表
     */
    List<OrderModificationRecord> getPendingList();

    /**
     * 按订单查询修改记录
     */
    List<OrderModificationRecord> getByOrderId(Long orderId);
}
