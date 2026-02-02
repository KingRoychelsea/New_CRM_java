-- CRM系统数据库建表SQL语句
-- 版本：MySQL 5.7+
-- 时间：2026-02-02

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS crm_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE crm_system;

-- 1. 用户表（users）
-- 存储系统用户信息，包含管理员和普通用户
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，自增主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一标识',
    password VARCHAR(100) NOT NULL COMMENT '密码，哈希加密存储',
    nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
    role VARCHAR(20) DEFAULT 'user' COMMENT '用户角色，如admin、user',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 索引优化：用户名是登录时的查询条件，添加索引提高查询速度
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 客户表（customers）
-- 存储客户基本信息，是CRM系统的核心表
CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID，自增主键',
    name VARCHAR(50) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    company VARCHAR(100) COMMENT '公司名称',
    position VARCHAR(50) COMMENT '职位',
    source VARCHAR(50) COMMENT '客户来源',
    notes TEXT COMMENT '备注信息',
    created_by INT COMMENT '创建人ID，关联users表',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 外键约束：关联创建人
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    -- 索引优化：
    -- 1. 姓名、手机号是常用查询条件，添加索引提高查询速度
    -- 2. 来源字段用于筛选，添加索引
    INDEX idx_name_phone (name, phone),
    INDEX idx_source (source),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 3. 跟进记录表（followups）
-- 存储客户跟进的详细记录
CREATE TABLE IF NOT EXISTS followups (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '跟进记录ID，自增主键',
    customer_id INT NOT NULL COMMENT '客户ID，关联customers表',
    user_id INT NOT NULL COMMENT '跟进人ID，关联users表',
    follow_time DATETIME NOT NULL COMMENT '跟进时间',
    follow_method VARCHAR(20) NOT NULL COMMENT '跟进方式：电话、微信、面谈',
    content TEXT NOT NULL COMMENT '跟进内容',
    next_follow_reminder DATETIME COMMENT '下次跟进提醒时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 外键约束：关联客户和跟进人
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    -- 索引优化：
    -- 1. 客户ID是最常用的查询条件，添加索引提高查询速度
    -- 2. 跟进时间用于排序和筛选，添加索引
    INDEX idx_customer_id (customer_id),
    INDEX idx_follow_time (follow_time),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跟进记录表';

-- 插入初始管理员账号
-- 密码：123456，使用MD5加密
INSERT INTO users (username, password, nickname, role) 
VALUES ('admin', MD5('123456'), '系统管理员', 'admin') 
ON DUPLICATE KEY UPDATE password = MD5('123456');

-- 表结构说明：
-- 1. 用户表（users）：存储系统登录用户信息，包含管理员和普通用户
-- 2. 客户表（customers）：存储客户基本信息，是CRM系统的核心数据
-- 3. 跟进记录表（followups）：存储与客户的沟通记录，关联到具体客户

-- 索引优化说明：
-- 1. 用户表：为username添加索引，优化登录查询
-- 2. 客户表：为name+phone组合添加索引，优化客户查询；为source添加索引，优化来源筛选
-- 3. 跟进记录表：为customer_id添加索引，优化按客户查询跟进记录；为follow_time添加索引，优化时间排序

-- 数据安全说明：
-- 1. 密码字段使用MD5加密存储，提高安全性
-- 2. 外键约束确保数据一致性
-- 3. 使用utf8mb4字符集，支持中文和特殊字符
