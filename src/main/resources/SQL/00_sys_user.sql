-- =============================================
-- 系统用户表
-- =============================================

CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username        VARCHAR(50)     NOT NULL COMMENT '用户名，唯一',
    password        VARCHAR(100)    NOT NULL COMMENT '密码（BCrypt加密）',
    real_name       VARCHAR(50)     NULL COMMENT '真实姓名',
    phone           VARCHAR(20)     NULL COMMENT '手机号',
    email           VARCHAR(100)    NULL COMMENT '邮箱',
    
    -- 状态字段
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    is_deleted      TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    
    -- 审计字段
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =============================================
-- 初始化测试数据
-- 默认密码都是：123456（BCrypt加密后）
-- =============================================

INSERT INTO sys_user (id, username, password, real_name, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6V.Le', '系统管理员', 1),
(2, 'test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6V.Le', '测试用户', 1)
ON DUPLICATE KEY UPDATE username=username;
