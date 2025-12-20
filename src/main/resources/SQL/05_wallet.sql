CREATE TABLE IF NOT EXISTS wallet (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    balance         BIGINT          NOT NULL DEFAULT 0 COMMENT '余额(分)',
    total_recharge  BIGINT          NOT NULL DEFAULT 0 COMMENT '累计充值金额(分)',
    total_consume   BIGINT          NOT NULL DEFAULT 0 COMMENT '累计消费金额(分)',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-冻结',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_wallet_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';
