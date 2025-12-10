CREATE TABLE IF NOT EXISTS charge_order (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    in_record_id    BIGINT          NOT NULL COMMENT '对应的入场记录ID，关联parking_record.id',
    amount          BIGINT          NOT NULL COMMENT '本次应收金额（分）',
    pay_status      VARCHAR(20)     NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付，PAID-已支付',
    out_time        DATETIME        NOT NULL COMMENT '本次计费对应的出场时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',

    PRIMARY KEY (id),
    KEY idx_in_record (in_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费订单表';