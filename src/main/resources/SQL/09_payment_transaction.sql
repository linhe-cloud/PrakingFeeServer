CREATE TABLE IF NOT EXISTS payment_transaction (
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id         BIGINT       NOT NULL COMMENT '关联收费订单ID，charge_order.id',
    order_no         VARCHAR(64)  NOT NULL COMMENT '业务订单号，冗余charge_order.order_no',
    user_id          BIGINT       NULL COMMENT '用户ID，可选',
    pay_channel      VARCHAR(20)  NOT NULL COMMENT '支付渠道：CASH/WECHAT/ALIPAY/WALLET/ETC',
    amount           BIGINT       NOT NULL COMMENT '本次支付金额(分)',
    third_trade_no   VARCHAR(64)  NULL COMMENT '第三方交易号/流水号',
    status           TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-INIT，1-SUCCESS，2-FAILED，3-REFUNDED',
    remark           VARCHAR(255) NULL COMMENT '备注，如失败原因',
    created_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_order_no (order_no),
    KEY idx_third_trade_no (third_trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水表';