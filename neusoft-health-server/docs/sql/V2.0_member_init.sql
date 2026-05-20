-- ============================================================
-- 东软智慧健康咨询系统 V2.0 会员模块数据库变更
-- 执行环境：MySQL 8.0+
-- 执行方式：mysql -h 60.215.128.117 -P 16642 -u root -p < V2.0_member_init.sql
-- ============================================================

-- 1. users 表新增字段（邀请+会员）
ALTER TABLE users
    ADD COLUMN member_level     VARCHAR(8)   DEFAULT 'L0'  COMMENT '当前会员等级：L0/L1/L2/L3',
    ADD COLUMN member_expire_time DATETIME   DEFAULT NULL  COMMENT '会员到期时间',
    ADD COLUMN invite_code      VARCHAR(8)   DEFAULT NULL  COMMENT '邀请码（8位唯一）',
    ADD COLUMN invited_by       BIGINT       DEFAULT NULL  COMMENT '邀请人ID',
    ADD COLUMN first_purchase   TINYINT      DEFAULT 0     COMMENT '是否已完成首购：0=否，1=是';

ALTER TABLE users
    ADD UNIQUE INDEX uk_invite_code (invite_code),
    ADD INDEX idx_invited_by (invited_by);

-- 2. 会员等级定义表
DROP TABLE IF EXISTS member_levels;
CREATE TABLE member_levels (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    level_code      VARCHAR(8)      NOT NULL              COMMENT '等级编码：L0/L1/L2/L3',
    level_name      VARCHAR(32)     NOT NULL              COMMENT '等级名称：普通用户/白银会员/黄金会员/铂金会员',
    daily_quota     INT             NOT NULL DEFAULT 0    COMMENT '每日咨询配额（0表示无限）',
    context_rounds  INT             NOT NULL DEFAULT 0    COMMENT '上下文轮数',
    auto_sync       TINYINT         DEFAULT 0             COMMENT '自动同步：0=否，1=是',
    deep_analysis   TINYINT         DEFAULT 0             COMMENT '深度分析：0=否，1=是',
    export_enabled  TINYINT         DEFAULT 0             COMMENT '导出功能：0=否，1=是',
    share_limit     INT             DEFAULT 0             COMMENT '共享人数限制（0表示无限）',
    sort_order      INT             DEFAULT 0             COMMENT '排序权重（升序）',
    status          TINYINT         DEFAULT 1             COMMENT '状态：0=禁用，1=启用',
    created_time    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0             COMMENT '逻辑删除：0=否，1=是',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_level_code (level_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员等级定义';

-- 3. 订阅方案表
DROP TABLE IF EXISTS subscription_plans;
CREATE TABLE subscription_plans (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    plan_code       VARCHAR(32)     NOT NULL              COMMENT '方案编码：L1_MONTHLY/L2_MONTHLY/L3_MONTHLY等',
    plan_name       VARCHAR(64)     NOT NULL              COMMENT '方案名称：白银月卡/黄金月卡等',
    level_code      VARCHAR(8)      NOT NULL              COMMENT '对应会员等级：L1/L2/L3',
    duration_days   INT             NOT NULL              COMMENT '有效期天数',
    price           DECIMAL(10,2)   NOT NULL              COMMENT '标准价格',
    original_price  DECIMAL(10,2)   DEFAULT NULL          COMMENT '首购体验价（仅首购用户可享）',
    sort_order      INT             DEFAULT 0             COMMENT '排序权重（升序）',
    status          TINYINT         DEFAULT 1             COMMENT '状态：0=下架，1=上架',
    created_time    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0             COMMENT '逻辑删除：0=否，1=是',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_plan_code (plan_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订阅方案';

-- 3. 支付订单表
DROP TABLE IF EXISTS payment_orders;
CREATE TABLE payment_orders (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no        VARCHAR(32)     NOT NULL              COMMENT '订单号（MP+时间戳+随机串）',
    user_id         BIGINT UNSIGNED NOT NULL              COMMENT '用户ID',
    plan_id         BIGINT UNSIGNED NOT NULL              COMMENT '订阅方案ID',
    amount          DECIMAL(10,2)   NOT NULL              COMMENT '支付金额',
    pay_method      VARCHAR(16)     DEFAULT 'alipay'      COMMENT '支付方式：alipay',
    pay_status      TINYINT         DEFAULT 0             COMMENT '支付状态：0=待支付，1=已支付，2=已取消，3=已退款',
    transaction_id  VARCHAR(64)     DEFAULT NULL          COMMENT '第三方交易流水号',
    paid_time       DATETIME        DEFAULT NULL          COMMENT '支付完成时间',
    expire_time     DATETIME        DEFAULT NULL          COMMENT '订单过期时间（创建后15分钟）',
    created_time    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0             COMMENT '逻辑删除：0=否，1=是',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_pay_status (pay_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付订单';

-- 4. 会员记录表
DROP TABLE IF EXISTS user_memberships;
CREATE TABLE user_memberships (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id         BIGINT UNSIGNED NOT NULL              COMMENT '用户ID',
    level_code      VARCHAR(8)      NOT NULL              COMMENT '会员等级：L1/L2/L3',
    start_time      DATETIME        NOT NULL              COMMENT '开始时间',
    expire_time     DATETIME        NOT NULL              COMMENT '到期时间',
    grace_end_time  DATETIME        DEFAULT NULL          COMMENT '宽限期截止时间（到期后+24h）',
    source_order_id BIGINT UNSIGNED DEFAULT NULL          COMMENT '来源订单ID（管理员开通则为NULL）',
    status          TINYINT         DEFAULT 1             COMMENT '状态：0=失效，1=生效',
    created_time    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0             COMMENT '逻辑删除：0=否，1=是',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员记录';

-- 5. 退款申请表
DROP TABLE IF EXISTS refund_requests;
CREATE TABLE refund_requests (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id        BIGINT UNSIGNED NOT NULL              COMMENT '订单ID',
    user_id         BIGINT UNSIGNED NOT NULL              COMMENT '用户ID',
    reason          TEXT            DEFAULT NULL          COMMENT '退款原因',
    refund_amount   DECIMAL(10,2)   NOT NULL              COMMENT '退款金额',
    status          TINYINT         DEFAULT 0             COMMENT '状态：0=待审核，1=已通过，2=已拒绝',
    handled_by      BIGINT UNSIGNED DEFAULT NULL          COMMENT '审核人ID',
    handle_remark   TEXT            DEFAULT NULL          COMMENT '审核备注',
    handled_time    DATETIME        DEFAULT NULL          COMMENT '审核时间',
    created_time    DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         DEFAULT 0             COMMENT '逻辑删除：0=否，1=是',
    PRIMARY KEY (id),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退款申请';

-- 7. 初始化会员等级数据
INSERT INTO member_levels (level_code, level_name, daily_quota, context_rounds, auto_sync, deep_analysis, export_enabled, share_limit, sort_order, status) VALUES
('L0', '普通用户',  3,   5, 0, 0, 0, 0, 0, 1),
('L1', '白银会员', 20,  15, 1, 0, 1, 1, 1, 1),
('L2', '黄金会员', 50,  30, 1, 0, 1, 3, 2, 1),
('L3', '铂金会员', 0,   50, 1, 1, 1, 0, 3, 1);

-- 8. 初始化订阅方案数据
INSERT INTO subscription_plans (plan_code, plan_name, level_code, duration_days, price, original_price, sort_order, status) VALUES
('L1_MONTHLY',  '白银月卡',  'L1', 30,  19.90, 0.10,  1, 1),
('L1_QUARTERLY','白银季卡',  'L1', 90,  49.90, NULL,  2, 1),
('L1_YEARLY',   '白银年卡',  'L1', 365, 169.00, NULL, 3, 1),
('L2_MONTHLY',  '黄金月卡',  'L2', 30,  39.90, 9.90,  4, 1),
('L2_QUARTERLY','黄金季卡',  'L2', 90,  99.90, NULL,  5, 1),
('L2_YEARLY',   '黄金年卡',  'L2', 365, 349.00, NULL, 6, 1),
('L3_MONTHLY',  '铂金月卡',  'L3', 30,  69.90, 19.90, 7, 1),
('L3_QUARTERLY','铂金季卡',  'L3', 90,  179.00, NULL, 8, 1),
('L3_YEARLY',   '铂金年卡',  'L3', 365, 599.00, NULL, 9, 1);