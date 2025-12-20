-- =============================================
-- 初始化脚本：创建 parking_manager 数据库
-- 说明：这个脚本需要手动在 MySQL 中执行一次
-- 执行方式：mysql -uroot -p < 00_create_database.sql
-- =============================================

CREATE DATABASE IF NOT EXISTS parking_manager
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

-- 验证数据库创建成功
SHOW DATABASES LIKE 'parking_manager';
