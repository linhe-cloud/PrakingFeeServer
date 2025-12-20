-- 订单优惠明细表：记录每个订单的优惠来源及金额
CREATE TABLE IF NOT EXISTS order_discount (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id        BIGINT          NOT NULL COMMENT '关联的订单ID，charge_order.id',
    source_type     VARCHAR(20)     NOT NULL COMMENT '优惠来源：MEMBER/RULE/COUPON/PROMO等',
    rule_code       VARCHAR(50)     NULL COMMENT '关联的规则/券编码',
    rule_name       VARCHAR(100)    NULL COMMENT '规则/券名称（冗余）',
    discount_type   VARCHAR(20)     NOT NULL COMMENT '优惠类型：PERCENT/FIXED/FREE等',
    discount_value  INT             NOT NULL COMMENT '优惠值：百分比(80=8折)/金额(分)/其他',
    discount_amount BIGINT          NOT NULL COMMENT '实际优惠金额(分)',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单优惠明细表';
