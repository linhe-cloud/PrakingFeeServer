-- 收费订单表：支持出场计费 + 幂等 + 支付信息 + 简单对账
CREATE TABLE IF NOT EXISTS charge_order (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    
    -- 业务订单号（对外展示/对账使用）
    order_no        VARCHAR(32)     NOT NULL COMMENT '业务订单号',
    
    -- 入场记录关联
    in_record_id    BIGINT          NOT NULL COMMENT '对应的入场记录ID，关联parking_record.id',
    
    -- 金额字段
    amount          BIGINT          NOT NULL COMMENT '本次应收金额（分）',
    
    -- 支付信息
    pay_status      VARCHAR(20)     NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付，PAID-已支付',
    pay_channel     VARCHAR(20)     NULL COMMENT '支付渠道：CASH/WECHAT/ALIPAY/ETC 等',
    pay_time        DATETIME        NULL COMMENT '支付时间',
    
    -- 冗余信息
    out_time        DATETIME        NOT NULL COMMENT '本次计费对应的出场时间',
    fee_rule_name   VARCHAR(100)    NULL COMMENT '计费规则名称（冗余，便于列表展示）',
    
    -- 审计字段
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',
    
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_in_record (in_record_id),
    UNIQUE KEY uk_in_record_out_time (in_record_id, out_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费订单表';