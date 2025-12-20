package com.charge.entity;

import java.time.LocalDateTime;

public class Wallet {
    private Long id;
    private Long userId;
    private Long balance;           // 余额(分)
    private Long totalRecharge;     // 累计充值(分)
    private Long totalConsume;      // 累计消费(分)
    private Integer status;         // 1-正常，0-冻结
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(Long totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public Long getTotalConsume() {
        return totalConsume;
    }

    public void setTotalConsume(Long totalConsume) {
        this.totalConsume = totalConsume;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
