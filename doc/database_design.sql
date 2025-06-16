-- Active: 1750038385033@@127.0.0.1@3306@handsome
-- 视频监控信息管理系统数据库设计
-- 数据库: handsome_video_monitor
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci

-- 1. 视频信息表
CREATE TABLE video_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '视频编号',
    video_name VARCHAR(255) NOT NULL COMMENT '视频名称',
    shooting_time DATETIME NOT NULL COMMENT '拍摄时间',
    shooting_location VARCHAR(255) NOT NULL COMMENT '拍摄地点',
    video_source VARCHAR(255) NOT NULL COMMENT '视频来源',
    file_path VARCHAR(500) COMMENT '视频文件路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    duration INT COMMENT '视频时长(秒)',
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_video_name (video_name),
    INDEX idx_shooting_time (shooting_time),
    INDEX idx_shooting_location (shooting_location),
    INDEX idx_video_source (video_source),
    INDEX idx_status (status)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='视频信息表-存储视频监控的基本信息';

-- 2. 视频分析结果表
CREATE TABLE video_analysis_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分析结果编号',
    video_id BIGINT NOT NULL COMMENT '视频编号',
    analysis_type VARCHAR(50) NOT NULL COMMENT '分析类型:face_recognition-人脸识别,behavior_analysis-行为分析,event_detection-事件检测',
    analysis_result JSON COMMENT '分析结果(JSON格式存储)',
    confidence_score DECIMAL(5,4) COMMENT '置信度分数(0-1)',
    analysis_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分析时间',
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_video_id (video_id),
    INDEX idx_analysis_type (analysis_type),
    INDEX idx_analysis_time (analysis_time),
    INDEX idx_status (status),
    FOREIGN KEY (video_id) REFERENCES video_info(id) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='视频分析结果表-存储视频分析的结果数据';

-- 3. 用户信息表
CREATE TABLE user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE INDEX uk_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户信息表-存储系统用户的基本信息和登录凭证';

-- 4. 角色表
CREATE TABLE role_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
    role_description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE INDEX uk_role_name (role_name),
    UNIQUE INDEX uk_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='角色信息表-存储系统角色定义信息';

-- 5. 权限表
CREATE TABLE permission_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) UNIQUE NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码',
    permission_description VARCHAR(255) COMMENT '权限描述',
    permission_type TINYINT DEFAULT 1 COMMENT '权限类型:1-菜单,2-按钮,3-接口',
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE INDEX uk_permission_name (permission_name),
    UNIQUE INDEX uk_permission_code (permission_code),
    INDEX idx_permission_type (permission_type),
    INDEX idx_status (status)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='权限信息表-存储系统权限定义信息';

-- 6. 用户角色关联表
CREATE TABLE user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    UNIQUE INDEX uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_info(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role_info(id) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户角色关联表-维护用户和角色的多对多关系';

-- 7. 角色权限关联表
CREATE TABLE role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    UNIQUE INDEX uk_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role_info(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission_info(id) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='角色权限关联表-维护角色和权限的多对多关系';

-- 8. 监控点位表
CREATE TABLE monitor_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '监控点ID',
    point_name VARCHAR(100) NOT NULL COMMENT '监控点名称',
    point_code VARCHAR(50) UNIQUE NOT NULL COMMENT '监控点编码',
    latitude DECIMAL(10,6) NOT NULL COMMENT '纬度',
    longitude DECIMAL(10,6) NOT NULL COMMENT '经度',
    address VARCHAR(255) COMMENT '详细地址',
    status TINYINT DEFAULT 1 COMMENT '状态:1-在线,0-离线,2-维护中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE INDEX uk_point_code (point_code),
    INDEX idx_point_name (point_name),
    INDEX idx_location (latitude, longitude),
    INDEX idx_status (status)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='监控点位表-存储监控设备的地理位置和状态信息';

-- 初始化数据

-- 插入默认角色
INSERT INTO role_info (role_name, role_code, role_description) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('监控管理员', 'MONITOR_ADMIN', '监控管理人员，负责视频监控管理'),
('安全管理员', 'SECURITY_ADMIN', '安全管理人员，负责安全相关功能'),
('数据分析员', 'DATA_ANALYST', '数据分析人员，负责视频分析和统计');

-- 插入默认权限
INSERT INTO permission_info (permission_name, permission_code, permission_description, permission_type) VALUES
-- 视频管理权限
('视频信息查询', 'video:query', '查询视频信息', 3),
('视频信息新增', 'video:add', '新增视频信息', 3),
('视频信息修改', 'video:edit', '修改视频信息', 3),
('视频信息删除', 'video:delete', '删除视频信息', 3),
('视频备份恢复', 'video:backup', '视频备份和恢复', 3),
-- 视频分析权限
('视频分析查询', 'analysis:query', '查询分析结果', 3),
('视频内容分析', 'analysis:content', '视频内容分析', 3),
('行为分析', 'analysis:behavior', '人员行为分析', 3),
('事件检测', 'analysis:event', '事件检测分析', 3),
('数据统计', 'analysis:statistics', '数据分析统计', 3),
-- 可视化权限
('地图展示', 'visual:map', '地图展示功能', 3),
('报表查看', 'visual:report', '报表查看功能', 3),
('实时预览', 'visual:preview', '实时视频预览', 3),
-- 用户管理权限
('用户管理', 'user:manage', '用户信息管理', 3),
('角色管理', 'role:manage', '角色管理', 3),
('权限管理', 'permission:manage', '权限管理', 3);

-- 为超级管理员分配所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission_info WHERE status = 1;

-- 插入默认超级管理员用户 (密码: admin123)
INSERT INTO user_info (username, password, real_name, email) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTcEp3mWkdTJdlI/8jJ0dCZ/7aS', '系统管理员', 'admin@example.com');

-- 为默认用户分配超级管理员角色
INSERT INTO user_role (user_id, role_id) VALUES (1, 1); 