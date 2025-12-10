package com.charge.entity.DTO;

import jakarta.validation.constraints.NotNull;


/**
 * 确认支付请求
 */
public class ConfirmPaymentRequest {
    /**
     * 收费订单ID
     */
    @NotNull(message = "收费订单ID不能为空")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
