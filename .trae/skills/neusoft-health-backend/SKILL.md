---
name: "neusoft-health-backend"
description: "东软智慧健康咨询系统后端开发指南。涵盖架构设计、模块说明、API文档、配置管理、数据库设计、安全RBAC和AI集成。在需要开发/维护此系统时调用。"
---

# 东软智慧健康咨询系统 - 后端开发指南

## 一、项目概述

基于 Spring Boot 3.3.x 的多模块微服务项目，为 UniApp 前端提供 RESTful API 服务。

### 核心能力

- AI 健康咨询（DeepSeek API 集成）
- 手机号验证码登录
- RBAC 权限控制
- 内容安全过滤 + 紧急情况识别
- 健康档案管理
- 管理后台（用户/审核/FAQ/统计/配置）

---

## 二、技术栈

| 组件 | 选型 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.3.x |
| JDK | Oracle OpenJDK | 17+ |
| ORM | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis (Lettuce) | 7.0 |
| 安全 | Spring Security + JWT | - |
| AI | DeepSeek API (REST) | V4 |
| 文档 | Knife4j (Swagger) | 4.5.0 |
| 验证 | Hibernate Validator | - |

---

## 三、模块结构

```
neusoft-health-server/
├── neusoft-health-api/                  # 启动入口
├── neusoft-health-common/               # 公共：R、枚举、工具类
├── neusoft-health-framework/            # 框架：Security、Config、AI、限流
├── neusoft-health-module-system/        # 系统：认证、首页、免责声明
├── neusoft-health-module-consultation/  # 咨询：会话、消息、FAQ、敏感词、应急
├── neusoft-health-module-user/          # 用户：健康档案、设置、收藏
├── neusoft-health-module-admin/         # 管理：用户管理、审核、统计、配置、日志
└── neusoft-health-module-security/      # 安全：角色、权限、RBAC
```

---

## 四、环境配置

### 4.1 .env 文件（项目根目录）

```properties
# ---- 数据库 ----
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=neusoft_health
MYSQL_USERNAME=root
MYSQL_PASSWORD=root

# ---- Redis ----
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0

# ---- JWT ----
JWT_SECRET=Y29tLm5ldXNvZnQuaGVhbHRoLWp3dC1zZWNyZXQta2V5LWZvci1wcm9kdWN0aW9u
JWT_EXPIRATION=86400000

# ---- 短信（阿里云 SMS）----
SMS_ACCESS_KEY=dummy-access-key
SMS_SECRET_KEY=dummy-secret-key
SMS_SIGN_NAME=东软健康
SMS_TEMPLATE_CODE=SMS_123456

# ---- DeepSeek AI ----
DEEPSEEK_API_KEY=sk-your-deepseek-api-key-here
DEEPSEEK_API_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-chat
```

### 4.2 application.yml 关键配置

所有敏感配置均通过 `${ENV_VAR:default}` 引用 .env 变量：

- `spring.datasource.*` -> `${MYSQL_*}`
- `spring.data.redis.*` -> `${REDIS_*}`
- `jwt.secret/expiration` -> `${JWT_*}`
- `deepseek.*` -> `${DEEPSEEK_*}`
- `sms.*` -> 直接配置（生产环境应改为 .env 变量）

---

## 五、数据库设计

### 5.1 系统模块

```sql
-- 用户表
CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '0=未知 1=男 2=女',
    birthday DATE,
    password VARCHAR(255) COMMENT 'BCrypt加密',
    status TINYINT DEFAULT 1 COMMENT '0=禁用 1=正常',
    last_login_time DATETIME,
    created_time DATETIME,
    updated_time DATETIME,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_phone(phone),
    INDEX idx_status(status)
) COMMENT '用户表';

-- 验证码表
CREATE TABLE sms_codes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    scene VARCHAR(20) DEFAULT 'LOGIN' COMMENT 'LOGIN/RESET',
    expire_time DATETIME NOT NULL,
    used TINYINT DEFAULT 0,
    created_time DATETIME,
    INDEX idx_phone_scene(phone, scene)
) COMMENT '短信验证码';

-- 用户登录日志
CREATE TABLE user_login_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    ip VARCHAR(50),
    device VARCHAR(200),
    login_time DATETIME,
    INDEX idx_user_id(user_id)
) COMMENT '登录日志';

-- 健康资讯
CREATE TABLE health_articles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    cover_url VARCHAR(500),
    content_url VARCHAR(500),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_time DATETIME,
    is_deleted TINYINT DEFAULT 0
) COMMENT '健康资讯';
```

### 5.2 咨询模块

```sql
-- 会话表
CREATE TABLE sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) DEFAULT '新会话',
    message_count INT DEFAULT 0,
    last_message_at DATETIME,
    status TINYINT DEFAULT 0 COMMENT '0=进行中 1=已结束',
    created_time DATETIME,
    updated_time DATETIME,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_user_id(user_id),
    INDEX idx_last_message(last_message_at)
) COMMENT '咨询会话表';

-- 消息表
CREATE TABLE messages (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'user/assistant/system',
    content TEXT NOT NULL,
    content_type TINYINT DEFAULT 1 COMMENT '1=文本',
    api_call_duration INT COMMENT 'API调用耗时ms',
    is_emergency TINYINT DEFAULT 0,
    emergency_keyword VARCHAR(100),
    is_violation TINYINT DEFAULT 0,
    violation_reason VARCHAR(500),
    review_status TINYINT DEFAULT 0 COMMENT '0=待审 1=通过 2=违规',
    reviewed_by BIGINT UNSIGNED,
    reviewed_at DATETIME,
    modified_content TEXT COMMENT '审核修改内容',
    created_time DATETIME,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_session_id(session_id),
    INDEX idx_user_id(user_id),
    INDEX idx_review_status(review_status)
) COMMENT '消息表';

-- FAQ分类表
CREATE TABLE faq_categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(200),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_time DATETIME,
    is_deleted TINYINT DEFAULT 0
) COMMENT 'FAQ分类';

-- FAQ表
CREATE TABLE faqs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT UNSIGNED NOT NULL,
    question VARCHAR(500) NOT NULL,
    answer TEXT,
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_time DATETIME,
    updated_time DATETIME,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_category_id(category_id)
) COMMENT '常见问题';

-- 敏感词表
CREATE TABLE sensitive_words (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(200) NOT NULL UNIQUE,
    category VARCHAR(50) COMMENT '政治/暴力/色情/医疗风险',
    severity TINYINT DEFAULT 1 COMMENT '1=普通 2=高风险',
    status TINYINT DEFAULT 1,
    created_time DATETIME,
    INDEX idx_category(category)
) COMMENT '敏感词';

-- 紧急日志表
CREATE TABLE emergency_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    message_id BIGINT UNSIGNED,
    keyword VARCHAR(100),
    handled TINYINT DEFAULT 0,
    handled_by BIGINT UNSIGNED,
    handled_at DATETIME,
    remark VARCHAR(500),
    created_time DATETIME,
    INDEX idx_user_id(user_id),
    INDEX idx_handled(handled)
) COMMENT '紧急情况日志';
```

### 5.3 用户模块

```sql
-- 健康档案表
CREATE TABLE user_health_profiles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    height DECIMAL(5,1),
    weight DECIMAL(5,1),
    blood_type VARCHAR(10) COMMENT 'A/B/AB/O/未知',
    allergies TEXT COMMENT '过敏史',
    medical_history TEXT COMMENT '既往病史',
    auto_sync TINYINT DEFAULT 1 COMMENT '是否自动同步给AI',
    created_time DATETIME,
    updated_time DATETIME,
    INDEX idx_user_id(user_id)
) COMMENT '健康档案';

-- 用户设置表
CREATE TABLE user_settings (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    notification_enabled TINYINT DEFAULT 1,
    voice_speed DECIMAL(2,1) DEFAULT 1.0,
    privacy_anonymous TINYINT DEFAULT 0,
    allow_recommend TINYINT DEFAULT 1,
    created_time DATETIME,
    updated_time DATETIME,
    INDEX idx_user_id(user_id)
) COMMENT '用户设置';

-- 收藏表
CREATE TABLE user_favorites (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    message_id BIGINT UNSIGNED,
    content TEXT,
    created_time DATETIME,
    INDEX idx_user_id(user_id),
    INDEX idx_message_id(message_id),
    UNIQUE KEY uk_user_message(user_id, message_id)
) COMMENT '用户收藏';
```

### 5.4 管理后台模块

```sql
-- 系统配置表
CREATE TABLE system_configs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(500),
    updated_by BIGINT UNSIGNED,
    updated_time DATETIME,
    INDEX idx_config_key(config_key)
) COMMENT '系统配置';

-- 操作日志表
CREATE TABLE operation_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    operator_id BIGINT UNSIGNED,
    operator_name VARCHAR(50),
    action VARCHAR(100) COMMENT '操作类型',
    target_type VARCHAR(50),
    target_id BIGINT UNSIGNED,
    detail TEXT,
    ip VARCHAR(50),
    created_time DATETIME,
    INDEX idx_operator(operator_id),
    INDEX idx_action(action),
    INDEX idx_created_time(created_time)
) COMMENT '操作日志';
```

### 5.5 安全模块

```sql
-- 角色表
CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(20) NOT NULL UNIQUE COMMENT 'R001-R004',
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_time DATETIME,
    updated_time DATETIME,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_role_code(role_code)
) COMMENT '角色表';
-- 预置数据：R001=普通用户, R002=超级管理员, R003=系统管理员, R004=内容审核员

-- 权限表
CREATE TABLE permissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    perm_code VARCHAR(100) NOT NULL UNIQUE,
    perm_name VARCHAR(100) NOT NULL,
    perm_type VARCHAR(20) COMMENT 'menu/button/data',
    parent_id BIGINT UNSIGNED DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_time DATETIME,
    updated_time DATETIME,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_parent_id(parent_id)
) COMMENT '权限表';

-- 用户角色关联表
CREATE TABLE user_roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    UNIQUE KEY uk_user_role(user_id, role_id),
    INDEX idx_user_id(user_id),
    INDEX idx_role_id(role_id)
) COMMENT '用户角色关联';

-- 角色权限关联表
CREATE TABLE role_permissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT UNSIGNED NOT NULL,
    perm_id BIGINT UNSIGNED NOT NULL,
    UNIQUE KEY uk_role_perm(role_id, perm_id),
    INDEX idx_role_id(role_id),
    INDEX idx_perm_id(perm_id)
) COMMENT '角色权限关联';
```

---

## 六、API 端点汇总

所有 API 以 `/api/v1/` 为前缀。

### 6.1 认证（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/auth/login/password | 密码登录 |
| POST | /api/v1/auth/login/sms-code | 短信验证码登录 |
| POST | /api/v1/auth/sms-code/send | 发送验证码 |
| POST | /api/v1/auth/sms-code/verify | 校验验证码 |
| POST | /api/v1/auth/token/refresh | 刷新令牌 |
| GET | /api/v1/home | 首页数据 |
| GET | /api/v1/disclaimer | 免责声明内容 |

### 6.2 用户（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/user/info | 获取个人信息 |
| PUT | /api/v1/user/info | 更新个人信息 |
| PUT | /api/v1/user/password | 修改密码 |
| GET | /api/v1/user/login-logs | 登录历史 |

### 6.3 健康档案/设置/收藏（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/user/health-profile | 获取健康档案 |
| PUT | /api/v1/user/health-profile | 更新健康档案 |
| GET | /api/v1/user/settings | 获取设置 |
| PUT | /api/v1/user/settings | 更新设置 |
| POST | /api/v1/user/favorite/toggle | 切换收藏 |
| GET | /api/v1/user/favorites | 收藏列表 |
| GET | /api/v1/user/favorite/check | 检查是否收藏 |

### 6.4 咨询（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/consultation/session | 创建会话 |
| GET | /api/v1/consultation/sessions | 会话列表 |
| DELETE | /api/v1/consultation/session/{id} | 删除会话 |
| POST | /api/v1/consultation/message | 发送消息（限流60次/分钟） |
| GET | /api/v1/consultation/session/{id}/messages | 消息列表 |
| GET | /api/v1/consultation/emergency/logs | 紧急日志 |
| POST | /api/v1/consultation/emergency/{id}/handle | 处理紧急 |
| GET | /api/v1/faq/categories | FAQ分类列表 |
| GET | /api/v1/faq/categories/tree | FAQ分类树 |
| GET | /api/v1/faq/category/{id} | 分类下FAQ列表 |

### 6.5 管理后台（需 R002/R003/R004 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/admin/users | 用户列表 |
| GET | /api/v1/admin/users/{id} | 用户详情 |
| PUT | /api/v1/admin/users/{id}/status | 禁用/启用用户 |
| DELETE | /api/v1/admin/users/{id} | 删除用户（R002） |
| GET | /api/v1/admin/review/pending | 待审核列表 |
| POST | /api/v1/admin/review/message | 审核消息（通过/违规） |
| GET | /api/v1/admin/faqs | FAQ管理列表 |
| POST | /api/v1/admin/faqs | 新增FAQ |
| PUT | /api/v1/admin/faqs/{id} | 编辑FAQ |
| DELETE | /api/v1/admin/faqs/{id} | 删除FAQ |
| GET | /api/v1/admin/faq-categories | FAQ分类管理 |
| POST | /api/v1/admin/faq-categories | 新增分类 |
| PUT | /api/v1/admin/faq-categories/{id} | 编辑分类 |
| DELETE | /api/v1/admin/faq-categories/{id} | 删除分类 |
| GET | /api/v1/admin/configs | 系统配置列表 |
| GET | /api/v1/admin/configs/{key} | 配置详情 |
| PUT | /api/v1/admin/configs | 更新配置 |
| GET | /api/v1/admin/stats/dashboard | 数据统计看板 |
| GET | /api/v1/admin/sensitive-words | 敏感词列表 |
| POST | /api/v1/admin/sensitive-words | 新增敏感词 |
| PUT | /api/v1/admin/sensitive-words/{id} | 编辑敏感词 |
| DELETE | /api/v1/admin/sensitive-words/{id} | 删除敏感词 |
| GET | /api/v1/admin/operation-logs | 操作日志列表 |

### 6.6 权限管理（需 R002/R003 角色）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/admin/roles | 角色列表 |
| GET | /api/v1/admin/roles/{id} | 角色详情（含权限） |
| GET | /api/v1/admin/roles/user/{userId} | 用户角色 |
| POST | /api/v1/admin/roles | 新增角色（R002） |
| PUT | /api/v1/admin/roles/{id} | 编辑角色（R002） |
| DELETE | /api/v1/admin/roles/{id} | 删除角色（R002） |
| PUT | /api/v1/admin/roles/{id}/permissions | 分配权限 |
| GET | /api/v1/admin/permissions | 权限列表 |
| GET | /api/v1/admin/permissions/tree | 权限树 |
| GET | /api/v1/admin/permissions/{id} | 权限详情 |
| POST | /api/v1/admin/permissions | 新增权限 |
| PUT | /api/v1/admin/permissions/{id} | 编辑权限 |
| DELETE | /api/v1/admin/permissions/{id} | 删除权限 |
| PUT | /api/v1/admin/user-roles/assign | 分配用户角色 |
| GET | /api/v1/admin/user-roles/{userId} | 用户角色查询 |

---

## 七、AI 集成（DeepSeek）

### 7.1 配置

```yaml
deepseek:
  api-key: ${DEEPSEEK_API_KEY}
  api-url: ${DEEPSEEK_API_URL:https://api.deepseek.com}
  model: ${DEEPSEEK_MODEL:deepseek-chat}
  max-tokens: 2048
  temperature: 0.7
```

### 7.2 调用流程

```
用户发送消息 -> 敏感词过滤 -> 紧急词检测 -> 保存用户消息
    -> 构建上下文（当前会话最近N条） -> 调用 DeepSeek API
    -> AI回复 -> 保存AI消息 -> 返回前端
```

### 7.3 注意事项

- API Key 仅配置在 YML 中，引用 .env 变量，不硬编码
- 调用超时 10 秒，自动重试 3 次
- 单用户限流 60 次/分钟（Redis 计数器实现）
- 紧急关键词触发全屏急救提示，替代 AI 回复
- 系统提示词禁止 AI 提供诊断/治疗/处方

---

## 八、安全设计

### 8.1 RBAC 权限控制

- SecurityConfig 按路径拦截：`/api/v1/admin/**` 需 R002/R003/R004
- 方法级注解：`@PreAuthorize("hasRole('R002')")` 用于敏感操作
- SecurityUtil 从 JWT 中提取用户ID

### 8.2 接口安全

- JWT 认证（7天有效期），无状态会话
- 密码 BCrypt 加密
- 敏感数据 AES-256 加密（AesUtil）
- 手机号脱敏显示（PhoneUtil.maskPhone）
- SQL 注入防护（MyBatis-Plus 参数绑定）
- Redis 限流 AOP 切面（RateLimitAspect）

---

## 九、开发规范

### 9.1 代码风格

- 遵循阿里巴巴 Java 开发手册（嵩山版）
- 统一使用 Lombok（`@Data`, `@RequiredArgsConstructor` 等）
- Controller 返回统一使用 `R.ok(data)` / `R.fail(msg)`
- 分页使用 `PageQueryDTO` + `PageResult`
- 枚举定义在 common 模块 enums 包下

### 9.2 包命名规范

```
com.neusoft.health.modules.{module}.controller
com.neusoft.health.modules.{module}.service
com.neusoft.health.modules.{module}.service.impl
com.neusoft.health.modules.{module}.mapper
com.neusoft.health.modules.{module}.entity
com.neusoft.health.modules.{module}.dto
com.neusoft.health.modules.{module}.vo
```

### 9.3 新增模块步骤

1. 在根 pom.xml 添加 `<module>` 声明
2. 在根 pom.xml dependencyManagement 添加依赖版本
3. 创建模块目录及 pom.xml（引用父模块）
4. 创建 entity -> mapper -> service -> controller
5. 在 api/pom.xml 添加模块依赖

---

## 十、部署指南

### 10.1 前置条件

- JDK 17+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.8+

### 10.2 构建

```bash
cd neusoft-health-server
mvn clean package -DskipTests
```

### 10.3 运行

```bash
# 复制 .env 到运行目录
cp .env neusoft-health-api/target/
cd neusoft-health-api/target
java -jar neusoft-health-api.jar
```

### 10.4 环境变量配置

生产环境需设置：

```bash
MYSQL_HOST=prod-db-host
MYSQL_PASSWORD=prod-password
DEEPSEEK_API_KEY=prod-key
JWT_SECRET=prod-jwt-secret
```

---

## 十一、初始数据

### 11.1 角色预置

```sql
INSERT INTO roles (role_code, role_name, description, sort_order) VALUES
('R001', '普通用户', 'C端普通用户', 1),
('R002', '超级管理员', '系统全面管理权限', 2),
('R003', '系统管理员', '用户管理/配置/统计', 3),
('R004', '内容审核员', '咨询审核/FAQ管理', 4);
```

### 11.2 超级管理员账号

```sql
-- 密码: admin123 (BCrypt加密)
INSERT INTO users (phone, nickname, password, status) VALUES
('13800000000', '超级管理员', '$2a$10$...', 1);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
```

---

## 十二、常见问题

**Q: 数据库连接失败？**
A: 检查 .env 中 MYSQL_HOST/PORT/DATABASE/USERNAME/PASSWORD 是否正确。

**Q: AI 回复为空？**
A: 检查 DEEPSEEK_API_KEY 是否有效，以及 deepseek.api-url 是否可访问。

**Q: 验证码无法发送？**
A: 检查 sms 配置，当前为演示模式（SmsUtil.java 默认返回 123456）。

**Q: 404 错误？**
A: 确认请求路径以 `/api/v1/` 为前缀，且已登录（携带 Authorization header）。
