CREATE TABLE IF NOT EXISTS charge_rule (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    parking_iot_id          BIGINT          NOT NULL COMMENT '适用车场ID',
    rule_name               VARCHAR(100)    NOT NULL COMMENT '规则名称',
    
    -- 适用范围（后面要按车种、车主类型扩展，可以先固定 ALL）
    car_type                VARCHAR(20)     NOT NULL DEFAULT 'ALL' COMMENT '车辆类型：ALL/SMALL/LARGE/NEW_ENERGY',
    
    -- 计费模式：先只支持 1-按时长统一单价
    charge_mode             TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '计费模式：1-按时长统一单价',
    
    -- 基础规则
    free_minutes            INT             NOT NULL DEFAULT 30 COMMENT '首免分钟数',
    unit_price              INT             NOT NULL COMMENT '基础单价（分/小时）',
    
    -- 封顶（可为 NULL）
    max_amount_per_day      INT             NULL COMMENT '每日封顶金额（分）',
    max_amount_per_session  INT             NULL COMMENT '单次封顶金额（分）',
    
    -- 生效日期（可空表示永久）
    effective_start_date    DATE            NULL COMMENT '规则生效起始日期',
    effective_end_date      DATE            NULL COMMENT '规则生效结束日期',
    
    -- 时段限制（例如：08:00-22:00，后续可扩展为多时段 JSON）
    effective_time_range    VARCHAR(50)     NULL COMMENT '如：08:00-22:00',
    
    priority                INT             NOT NULL DEFAULT 0 COMMENT '优先级，数值越大越优先',
    status                  TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '1-启用，0-禁用',
    
    create_time             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (id),
    KEY idx_parking_iot_car_type_status (parking_iot_id, car_type, status),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计费规则主表';