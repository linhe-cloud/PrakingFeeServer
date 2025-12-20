package com.charge.service.impl;

import com.charge.entity.ChargeOrder;
import com.charge.entity.OrderModificationRecord;
import com.charge.mapper.ChargeOrderMapper;
import com.charge.mapper.OrderModificationRecordMapper;
import com.charge.service.OrderModificationService;
import com.charge.service.PaymentService;
import com.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderModificationServiceImpl implements OrderModificationService {

    private final OrderModificationRecordMapper modificationRecordMapper;
    private final ChargeOrderMapper chargeOrderMapper;
    private final PaymentService paymentService;

    public OrderModificationServiceImpl(OrderModificationRecordMapper modificationRecordMapper,
                                        ChargeOrderMapper chargeOrderMapper,
                                        PaymentService paymentService) {
        this.modificationRecordMapper = modificationRecordMapper;
        this.chargeOrderMapper = chargeOrderMapper;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public OrderModificationRecord applyAmountAdjust(Long orderId,
                                                     Long afterAmount,
                                                     String reason,
                                                     Long applyUserId,
                                                     String applyUserName) {
        ChargeOrder order = chargeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        OrderModificationRecord record = new OrderModificationRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setModifyType("AMOUNT_ADJUST");
        record.setBeforeAmount(order.getAmount());
        record.setAfterAmount(afterAmount);
        record.setReason(reason);
        record.setApplyUserId(applyUserId);
        record.setApplyUserName(applyUserName);
        record.setStatus("PENDING");

        modificationRecordMapper.insert(record);
        return record;
    }

    @Override
    @Transactional
    public OrderModificationRecord applyRefund(Long orderId,
                                               String reason,
                                               Long applyUserId,
                                               String applyUserName) {
        ChargeOrder order = chargeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PAID".equals(order.getPayStatus())) {
            throw new BusinessException("只有已支付订单才能退款");
        }

        OrderModificationRecord record = new OrderModificationRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setModifyType("REFUND");
        record.setBeforeAmount(order.getAmount());
        record.setAfterAmount(0L);
        record.setReason(reason);
        record.setApplyUserId(applyUserId);
        record.setApplyUserName(applyUserName);
        record.setStatus("PENDING");

        modificationRecordMapper.insert(record);
        return record;
    }

    @Override
    @Transactional
    public void approve(Long recordId,
                        Long approveUserId,
                        String approveUserName,
                        String approveRemark) {
        OrderModificationRecord record = modificationRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("修改记录不存在");
        }
        if (!"PENDING".equals(record.getStatus())) {
            throw new BusinessException("该申请已处理，无法重复审批");
        }

        // 更新审批状态
        record.setStatus("APPROVED");
        record.setApproveUserId(approveUserId);
        record.setApproveUserName(approveUserName);
        record.setApproveTime(LocalDateTime.now());
        record.setApproveRemark(approveRemark);
        modificationRecordMapper.updateStatus(record);

        // 执行实际修改
        ChargeOrder order = chargeOrderMapper.selectById(record.getOrderId());
        if (order == null) {
            throw new BusinessException("关联订单不存在");
        }

        if ("AMOUNT_ADJUST".equals(record.getModifyType())) {
            // 改价
            order.setAmount(record.getAfterAmount());
            chargeOrderMapper.updateById(order);

        } else if ("REFUND".equals(record.getModifyType())) {

            // TODO 退款流程不完整
            // 退款：调用支付服务的退款逻辑
            // 这里简化处理，实际应该找到对应的支付流水ID
            order.setPayStatus("REFUNDED");
            chargeOrderMapper.updateById(order);
            // 如果需要调用 PaymentService.markRefund，需要先找到 transactionId
            // 这里暂时只改订单状态，实际可以通过 PaymentTransactionMapper 查询
        }
    }

    @Override
    @Transactional
    public void reject(Long recordId,
                       Long approveUserId,
                       String approveUserName,
                       String approveRemark) {
        OrderModificationRecord record = modificationRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("修改记录不存在");
        }
        if (!"PENDING".equals(record.getStatus())) {
            throw new BusinessException("该申请已处理，无法重复审批");
        }

        record.setStatus("REJECTED");
        record.setApproveUserId(approveUserId);
        record.setApproveUserName(approveUserName);
        record.setApproveTime(LocalDateTime.now());
        record.setApproveRemark(approveRemark);
        modificationRecordMapper.updateStatus(record);
    }

    @Override
    public List<OrderModificationRecord> getPendingList() {
        return modificationRecordMapper.selectPendingList();
    }

    @Override
    public List<OrderModificationRecord> getByOrderId(Long orderId) {
        return modificationRecordMapper.selectByOrderId(orderId);
    }
}
