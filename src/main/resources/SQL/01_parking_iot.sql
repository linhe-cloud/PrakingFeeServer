CREATE TABLE IF NOT EXISTS parking_lot (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    code            VARCHAR(50)     NOT NULL COMMENT '车场编码，唯一',
    name            VARCHAR(100)    NOT NULL COMMENT '车场名称',

    -- 计费相关
    billing_type    TINYINT         NOT NULL DEFAULT 1 COMMENT '计费模式：1-按小时统一单价，2-首小时优惠等',
    unit_price      INT             NOT NULL COMMENT '基础单价（分/小时），例如500=5元/小时',
    max_daily_amount INT            NULL COMMENT '每日费用封顶（分），null表示不封顶',
    free_minutes    INT             NOT NULL DEFAULT 0 COMMENT '免费时长（分钟），默认0',

    -- 运营信息
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    open_time       VARCHAR(50)     NULL COMMENT '开放时间说明，如：08:00-22:00',
    remark          VARCHAR(255)    NULL COMMENT '备注',

    -- 审计字段
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(50)     NULL COMMENT '创建人',
    updated_by      VARCHAR(50)     NULL COMMENT '更新人',

    PRIMARY KEY (id),
    UNIQUE KEY uk_parking_lot_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车场基础信息表';