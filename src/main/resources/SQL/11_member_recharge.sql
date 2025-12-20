CREATE TABLE IF NOT EXISTS member_recharge (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '充值记录ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    recharge_no     VARCHAR(64)     NOT NULL COMMENT '充值单号',
    amount          BIGINT          NOT NULL COMMENT '充值金额(分)',
    bonus_amount    BIGINT          NOT NULL DEFAULT 0 COMMENT '赠送金额(分)',
    total_amount    BIGINT          NOT NULL COMMENT '实际到账金额(分) = amount + bonus_amount',
    pay_channel     VARCHAR(20)     NULL COMMENT '支付渠道：ALIPAY/WECHAT/BALANCE等',
    pay_status      TINYINT         NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-已支付，2-已退款',
    pay_time        DATETIME        NULL COMMENT '支付时间',
    member_upgraded TINYINT         NOT NULL DEFAULT 0 COMMENT '是否触发会员升级：0-否，1-是',
    remark          VARCHAR(255)    NULL COMMENT '备注',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_recharge_no (recharge_no),
    KEY idx_user_id (user_id),
    KEY idx_pay_status (pay_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员充值记录表';
