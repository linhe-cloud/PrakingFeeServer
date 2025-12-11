CREATE TABLE IF NOT EXISTS parking_record (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plate_number    VARCHAR(20)     NOT NULL COMMENT '车牌号',
    parking_lot_id  BIGINT          NOT NULL COMMENT '车场ID，关联parking_lot.id',
    in_time         DATETIME        NOT NULL COMMENT '入场时间',
    out_time        DATETIME        NULL COMMENT '出场时间，未出场为NULL',
    paid_amount     BIGINT          NULL COMMENT '已支付金额（分）',
    status          VARCHAR(20)     NOT NULL DEFAULT 'IN' COMMENT '状态：IN-在场，FINISHED-已完成',
    remark          VARCHAR(255)    NULL COMMENT '备注',

    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    KEY idx_plate_number (plate_number),
    KEY idx_parking_lot (parking_lot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入场记录表';