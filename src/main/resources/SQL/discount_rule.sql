-- 优惠规则表：支持折扣百分比和固定金额
CREATE TABLE IF NOT EXISTS discount_rule (
    id              BIGINT           NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    rule_code       VARCHAR(50)      NOT NULL COMMENT '优惠规则编码，唯一',
    rule_name       VARCHAR(100)     NOT NULL COMMENT '优惠规则名称',
    description     VARCHAR(255)     NULL COMMENT '优惠说明',
    
    -- 优惠类型：百分比或固定金额
    discount_type   VARCHAR(20)      NOT NULL COMMENT '优惠类型：PERCENT(折扣百分比)/FIXED(固定金额)',
    discount_value  INT              NOT NULL COMMENT '优惠值：百分比(如80表示8折)/金额(分)',
    max_discount    INT              NULL COMMENT '单笔最大优惠金额(分)，可为空',
    
    status          TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    effective_start DATETIME         NULL COMMENT '生效开始时间',
    effective_end   DATETIME         NULL COMMENT '生效结束时间',
    
    create_time     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_code (rule_code),
    KEY idx_status_effective (status, effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠规则表';