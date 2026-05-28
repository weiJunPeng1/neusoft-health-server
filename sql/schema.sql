-- ============================================================
-- 东软智慧健康咨询系统 V1.0 数据库初始化DDL
-- 数据库: MySQL 8.0
-- 编码: utf8mb4
-- 命名规范: 表名小写复数、字段名小写单数、下划线分隔
-- ============================================================

CREATE DATABASE IF NOT EXISTS neusoft_health
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE neusoft_health;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE users (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    phone_hash      CHAR(64)        NOT NULL                COMMENT '手机号SHA-256哈希，用于唯一索引',
    phone_encrypted VARCHAR(255)    NOT NULL                COMMENT '手机号AES-256加密',
    password_hash   VARCHAR(255)    DEFAULT NULL            COMMENT 'BCrypt密码哈希（可选）',
    nickname        VARCHAR(64)     DEFAULT ''              COMMENT '昵称',
    avatar_url      VARCHAR(512)    DEFAULT ''              COMMENT '头像URL',
    gender          TINYINT         DEFAULT 0               COMMENT '性别: 0=未知, 1=男, 2=女',
    age             INT             DEFAULT 0               COMMENT '年龄',
    open_id         VARCHAR(128)    DEFAULT ''              COMMENT '第三方登录OpenID',
    login_type      VARCHAR(16)     DEFAULT 'sms'           COMMENT '注册方式: sms/wechat/alipay',
    disclaimer_accepted TINYINT     DEFAULT 0               COMMENT '是否已确认免责声明',
    last_login_time DATETIME        DEFAULT NULL            COMMENT '最后登录时间',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 0=禁用, 1=正常',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除: 0=否, 1=是',
    UNIQUE INDEX uk_phone_hash (phone_hash),
    INDEX idx_status (status),
    INDEX idx_created_time (created_time),
    INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 用户健康档案表
-- ============================================================
CREATE TABLE user_health_profiles (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    user_id         BIGINT UNSIGNED NOT NULL                COMMENT '用户ID',
    height          DECIMAL(5,2)    DEFAULT NULL            COMMENT '身高(cm)',
    weight          DECIMAL(5,2)    DEFAULT NULL            COMMENT '体重(kg)',
    blood_type      VARCHAR(4)      DEFAULT NULL            COMMENT '血型: A/B/AB/O/未知',
    allergies       TEXT                                    COMMENT '过敏史(AES加密)',
    medical_history TEXT                                    COMMENT '既往病史(AES加密)',
    auto_sync       TINYINT         DEFAULT 0               COMMENT '是否自动同步给AI',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户健康档案表';

-- ============================================================
-- 3. 用户设置表
-- ============================================================
CREATE TABLE user_settings (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '设置ID',
    user_id             BIGINT UNSIGNED NOT NULL            COMMENT '用户ID',
    notification_enabled TINYINT        DEFAULT 1           COMMENT '通知开关',
    voice_speed         DECIMAL(3,2)    DEFAULT 1.00        COMMENT '语音播报语速',
    voice_volume        INT             DEFAULT 80          COMMENT '语音音量',
    voice_tone          VARCHAR(32)     DEFAULT 'default'   COMMENT '语音音色',
    anonymous_mode      TINYINT         DEFAULT 0           COMMENT '匿名咨询模式',
    recommend_enabled   TINYINT         DEFAULT 1           COMMENT '是否允许推荐',
    created_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted          TINYINT         DEFAULT 0           COMMENT '逻辑删除',
    UNIQUE INDEX uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';

-- ============================================================
-- 4. 角色表
-- ============================================================
CREATE TABLE roles (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_code       VARCHAR(32)     NOT NULL                COMMENT '角色编码: R001=普通用户,R002=超级管理员,R003=系统管理员,R004=内容审核员',
    role_name       VARCHAR(64)     NOT NULL                COMMENT '角色名称',
    description     VARCHAR(255)    DEFAULT ''              COMMENT '角色描述',
    sort_order      INT             DEFAULT 0               COMMENT '排序号',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 0=禁用, 1=正常',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    UNIQUE INDEX uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================================
-- 5. 权限表
-- ============================================================
CREATE TABLE permissions (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    perm_code       VARCHAR(64)     NOT NULL                COMMENT '权限编码',
    perm_name       VARCHAR(64)     NOT NULL                COMMENT '权限名称',
    perm_type       VARCHAR(16)     NOT NULL DEFAULT 'menu' COMMENT '权限类型: menu/button/data',
    parent_id       BIGINT UNSIGNED DEFAULT 0               COMMENT '父权限ID',
    sort_order      INT             DEFAULT 0               COMMENT '排序号',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    UNIQUE INDEX uk_perm_code (perm_code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ============================================================
-- 6. 用户角色关联表
-- ============================================================
CREATE TABLE user_roles (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id         BIGINT UNSIGNED NOT NULL                COMMENT '用户ID',
    role_id         BIGINT UNSIGNED NOT NULL                COMMENT '角色ID',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    UNIQUE INDEX uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ============================================================
-- 7. 角色权限关联表
-- ============================================================
CREATE TABLE role_permissions (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    role_id         BIGINT UNSIGNED NOT NULL                COMMENT '角色ID',
    perm_id         BIGINT UNSIGNED NOT NULL                COMMENT '权限ID',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_role_id (role_id),
    INDEX idx_perm_id (perm_id),
    UNIQUE INDEX uk_role_perm (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================================
-- 8. 咨询会话表
-- ============================================================
CREATE TABLE sessions (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    user_id         BIGINT UNSIGNED NOT NULL                COMMENT '用户ID',
    title           VARCHAR(255)    DEFAULT ''              COMMENT '会话标题(首条问题前20字)',
    message_count   INT             DEFAULT 0               COMMENT '消息数量',
    last_message_at DATETIME        DEFAULT NULL            COMMENT '最后消息时间',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 1=进行中, 0=已结束',
    category        VARCHAR(16)     DEFAULT ''              COMMENT '咨询分类(内科/外科/儿科/...由AI自动识别)',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_user_last_msg (user_id, last_message_at),
    INDEX idx_last_message_at (last_message_at),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询会话表';

-- ============================================================
-- 9. 咨询消息表
-- ============================================================
CREATE TABLE messages (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    session_id      BIGINT UNSIGNED NOT NULL                COMMENT '会话ID',
    user_id         BIGINT UNSIGNED NOT NULL                COMMENT '用户ID',
    role            VARCHAR(16)     NOT NULL                COMMENT '角色: user/assistant/system',
    content         TEXT            NOT NULL                COMMENT '消息内容',
    content_type    TINYINT         DEFAULT 1               COMMENT '内容类型: 1=文本',
    is_emergency    TINYINT         DEFAULT 0               COMMENT '是否触发紧急流程',
    emergency_keyword VARCHAR(255)  DEFAULT ''              COMMENT '触发的紧急关键词',
    is_violation    TINYINT         DEFAULT 0               COMMENT '是否违规',
    violation_reason VARCHAR(255)   DEFAULT ''              COMMENT '违规原因',
    review_status   TINYINT         DEFAULT 0               COMMENT '审核状态: 0=待审核, 1=通过, 2=违规',
    reviewed_by     BIGINT UNSIGNED DEFAULT NULL            COMMENT '审核人ID',
    reviewed_at     DATETIME        DEFAULT NULL            COMMENT '审核时间',
    modified_content TEXT                                   COMMENT '人工修改后的内容',
    api_call_duration INT           DEFAULT NULL            COMMENT 'DeepSeek API调用耗时(ms)',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_review_status (review_status, created_time),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询消息表';

-- ============================================================
-- 10. FAQ分类表
-- ============================================================
CREATE TABLE faq_categories (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name            VARCHAR(64)     NOT NULL                COMMENT '分类名称',
    icon            VARCHAR(255)    DEFAULT ''              COMMENT '图标URL',
    sort_order      INT             DEFAULT 0               COMMENT '排序号',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 0=禁用, 1=正常',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_sort_order (sort_order),
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FAQ分类表';

-- ============================================================
-- 11. 常见问题表
-- ============================================================
CREATE TABLE faqs (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '问题ID',
    category_id     BIGINT UNSIGNED NOT NULL                COMMENT '分类ID',
    question        VARCHAR(500)    NOT NULL                COMMENT '问题内容',
    preset_answer   TEXT                                    COMMENT '预设答案',
    sort_order      INT             DEFAULT 0               COMMENT '排序号',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 0=禁用, 1=正常',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_category_id (category_id),
    INDEX idx_category_sort (category_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常见问题表';

-- ============================================================
-- 12. 敏感词表
-- ============================================================
CREATE TABLE sensitive_words (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '敏感词ID',
    word            VARCHAR(128)    NOT NULL                COMMENT '敏感词',
    category        VARCHAR(32)     DEFAULT ''              COMMENT '分类: 政治/色情/暴力/医疗风险/违法',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 0=禁用, 1=正常',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_word (word),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词表';

-- ============================================================
-- 13. 用户收藏表
-- ============================================================
CREATE TABLE user_favorites (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id         BIGINT UNSIGNED NOT NULL                COMMENT '用户ID',
    message_id      BIGINT UNSIGNED NOT NULL                COMMENT '消息ID',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    UNIQUE INDEX uk_user_message (user_id, message_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- ============================================================
-- 14. 系统配置表
-- ============================================================
CREATE TABLE system_configs (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key      VARCHAR(64)     NOT NULL                COMMENT '配置键',
    config_value    TEXT            NOT NULL                COMMENT '配置值',
    config_type     VARCHAR(16)     DEFAULT 'string'        COMMENT '值类型: string/number/json/encrypted',
    description     VARCHAR(255)    DEFAULT ''              COMMENT '配置说明',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    UNIQUE INDEX uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================================
-- 15. 短信验证码表
-- ============================================================
CREATE TABLE sms_codes (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '验证码ID',
    phone_hash      CHAR(64)        NOT NULL                COMMENT '手机号SHA-256哈希',
    code            VARCHAR(10)     NOT NULL                COMMENT '验证码',
    expire_time     DATETIME        NOT NULL                COMMENT '过期时间',
    used            TINYINT         DEFAULT 0               COMMENT '是否已使用',
    send_ip         VARCHAR(64)     DEFAULT ''              COMMENT '发送IP',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_phone_hash (phone_hash),
    INDEX idx_phone_expire (phone_hash, expire_time),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';

-- ============================================================
-- 16. 紧急情况日志表
-- ============================================================
CREATE TABLE emergency_logs (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id         BIGINT UNSIGNED DEFAULT NULL            COMMENT '用户ID',
    message_id      BIGINT UNSIGNED DEFAULT NULL            COMMENT '消息ID',
    keyword_matched VARCHAR(255)    DEFAULT ''              COMMENT '匹配到的紧急关键词',
    handled         TINYINT         DEFAULT 1               COMMENT '处理状态: 1=已处理',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='紧急情况日志表';

-- ============================================================
-- 17. 用户登录日志表
-- ============================================================
CREATE TABLE user_login_logs (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id         BIGINT UNSIGNED DEFAULT NULL            COMMENT '用户ID',
    login_type      VARCHAR(16)     DEFAULT 'sms'           COMMENT '登录方式: sms/wechat/alipay',
    login_ip        VARCHAR(64)     DEFAULT ''              COMMENT '登录IP',
    user_agent      VARCHAR(512)    DEFAULT ''              COMMENT 'User-Agent',
    login_result    TINYINT         DEFAULT 1               COMMENT '登录结果: 1=成功, 0=失败',
    fail_reason     VARCHAR(255)    DEFAULT ''              COMMENT '失败原因',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_created_time (created_time),
    INDEX idx_user_login (user_id, created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录日志表';

-- ============================================================
-- 18. 健康资讯表
-- ============================================================
CREATE TABLE health_articles (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title           VARCHAR(255)    NOT NULL                COMMENT '文章标题',
    summary         VARCHAR(512)    DEFAULT ''              COMMENT '文章摘要',
    cover_url       VARCHAR(512)    DEFAULT ''              COMMENT '封面图URL',
    content_url     VARCHAR(512)    DEFAULT ''              COMMENT 'H5详情页URL',
    sort_order      INT             DEFAULT 0               COMMENT '排序号',
    status          TINYINT         DEFAULT 1               COMMENT '状态: 0=禁用, 1=正常',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康资讯表';

-- ============================================================
-- 19. 操作日志表
-- ============================================================
CREATE TABLE operation_logs (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id         BIGINT UNSIGNED DEFAULT NULL            COMMENT '操作用户ID',
    username        VARCHAR(64)     DEFAULT ''              COMMENT '操作用户名',
    operation       VARCHAR(64)     NOT NULL                COMMENT '操作类型',
    module          VARCHAR(64)     DEFAULT ''              COMMENT '操作模块',
    target_type     VARCHAR(32)     DEFAULT ''              COMMENT '操作对象类型',
    target_id       VARCHAR(64)     DEFAULT ''              COMMENT '操作对象ID',
    request_method  VARCHAR(16)     DEFAULT ''              COMMENT '请求方法',
    request_url     VARCHAR(512)    DEFAULT ''              COMMENT '请求URL',
    request_params  TEXT                                    COMMENT '请求参数',
    response_result TEXT                                    COMMENT '响应结果(脱敏)',
    ip_address      VARCHAR(64)     DEFAULT ''              COMMENT 'IP地址',
    user_agent      VARCHAR(512)    DEFAULT ''              COMMENT 'User-Agent',
    duration        INT             DEFAULT 0               COMMENT '执行耗时(ms)',
    status          TINYINT         DEFAULT 1               COMMENT '执行状态: 1=成功, 0=失败',
    error_msg       TEXT                                    COMMENT '错误信息',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted      TINYINT         DEFAULT 0               COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_created_time (created_time),
    INDEX idx_module (module, created_time),
    INDEX idx_operation (operation, created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================================
-- V2.0 会员与邀请模块新增字段
-- ============================================================
ALTER TABLE users ADD COLUMN member_level VARCHAR(8) DEFAULT 'L0' COMMENT '当前会员等级' AFTER is_deleted;
ALTER TABLE users ADD COLUMN member_expire_time DATETIME DEFAULT NULL COMMENT '会员到期时间' AFTER member_level;
ALTER TABLE users ADD COLUMN invite_code VARCHAR(16) DEFAULT NULL COMMENT '邀请码(8位唯一)' AFTER member_expire_time;
ALTER TABLE users ADD COLUMN invited_by BIGINT UNSIGNED DEFAULT NULL COMMENT '邀请人ID' AFTER invite_code;
ALTER TABLE users ADD COLUMN first_purchase TINYINT DEFAULT 0 COMMENT '是否已完成首购(0=否 1=是)' AFTER invited_by;

ALTER TABLE users ADD UNIQUE INDEX uk_invite_code (invite_code);
ALTER TABLE users ADD INDEX idx_invited_by (invited_by);
