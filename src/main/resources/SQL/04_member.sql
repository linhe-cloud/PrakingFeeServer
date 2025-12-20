-- 会员信息表
CREATE TABLE IF NOT EXISTS member (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID或账号ID',
    level           TINYINT         NOT NULL DEFAULT 0 COMMENT '会员等级：0-普通，1-VIP，2-黑金等',
    discount_rate   DECIMAL(5,2)    NOT NULL DEFAULT 1.00 COMMENT '折扣系数：1.00不打折，0.80表示8折',
    free_parking    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否享受免费停车：0-否，1-是',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-有效，0-失效',
    remark          VARCHAR(255)    NULL COMMENT '备注',

    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_member_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';
