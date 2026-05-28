# 东软智慧健康咨询系统 — 功能补全规划方案

> **版本**: v1.0  
> **日期**: 2026-05-28  
> **状态**: 待审核

---

## 一、现状分析

### 1.1 已完成功能

| 模块 | 后端 | 用户端(UniApp) | 管理端(Web) |
|------|------|----------------|-------------|
| 认证登录 | ✅ 短信/密码登录 | ✅ 登录页 | ✅ 登录页 |
| AI健康咨询 | ✅ DeepSeek集成 | ✅ 对话页 | — |
| 会话管理 | ✅ CRUD | ✅ 历史列表 | — |
| FAQ常见问题 | ✅ 分类+问答 | ✅ FAQ页 | ✅ FAQ管理 |
| 健康搜索 | ✅ 语义缓存 | ✅ 搜索页 | — |
| 健康档案 | ✅ CRUD | ✅ 档案页 | — |
| 用户设置 | ✅ CRUD | ✅ 设置页 | — |
| 收藏功能 | ✅ 切换/列表 | ✅ 收藏页 | — |
| 会员等级 | ✅ L0-L3权益 | ✅ 会员页 | ✅ 会员管理 |
| 支付订单 | ✅ 支付宝集成 | ✅ 订单页 | ✅ 订单管理 |
| 退款审核 | ✅ 审核流程 | ✅ 退款申请 | ✅ 退款审核 |
| 内容审核 | ✅ 敏感词拦截 | — | ✅ 审核列表 |
| 敏感词管理 | ✅ CRUD | — | ✅ 管理页 |
| 系统配置 | ✅ KV配置 | — | ✅ 配置页 |
| 权限管理 | ✅ RBAC | — | ✅ 角色/权限 |
| 操作日志 | ✅ AOP自动记录 | — | ✅ 日志页 |
| 数据大屏 | ✅ 统计接口 | — | ✅ 大屏页 |
| 紧急预警 | ✅ 关键词检测 | ✅ 全屏弹窗 | — |
| 健康资讯 | ✅ 文章表 | ✅ 资讯页 | — |
| 药品搜索 | ✅ AI搜索 | ✅ 药品页 | — |

### 1.2 功能缺口

| # | 缺口 | 优先级 | 影响范围 |
|---|------|--------|----------|
| 1 | 用户注册页 | P0 | 用户端 |
| 2 | 用户个人资料编辑页 | P0 | 用户端 |
| 3 | 用户通知系统 | P0 | 全局 |
| 4 | 管理员公告管理 | P1 | 管理端+用户端 |
| 5 | 管理员账号管理（超级管理员增删审核员/系统管理员） | P1 | 管理端 |
| 6 | 用户端支付页面清理（移除不必要的支付结算入口） | P1 | 用户端 |

---

## 二、功能详细设计

### 2.1 用户注册页（P0）

**现状**: 用户端只有登录页（短信验证码登录），没有独立的注册入口。新用户通过登录页自动注册（首次登录自动创建账号）。

**问题**: 用户无法主动设置昵称、头像等个人信息，首次登录后全是默认值。

**方案**: 在登录流程后增加**新用户引导页**，而非独立注册页。

```
用户输入手机号 → 发送验证码 → 验证通过
  ├─ 老用户 → 直接进入首页
  └─ 新用户 → 跳转"完善信息"引导页 → 设置昵称/头像/性别 → 进入首页
```

**数据库**: 无需新建表，复用 `users` 表。

**接口**: 无需新建，复用现有登录接口 + `PUT /api/v1/user/profile`。

**前端**:
- 新建 `pages/guide/index.vue` — 新用户引导页
- 修改登录页逻辑：登录后检查是否新用户（nickname 为空），是则跳转引导页

---

### 2.2 用户个人资料编辑页（P0）

**现状**: 用户端有 `pages/profile/index.vue` 和 `pages/settings/index.vue`，但功能不完整。

**方案**: 完善个人资料页，支持编辑以下信息：

| 字段 | 说明 | 修改方式 |
|------|------|----------|
| 昵称 | 用户昵称 | 文本输入 |
| 头像 | 用户头像 | 相册/拍照上传 |
| 性别 | 男/女/未知 | 单选 |
| 生日 | 出生日期 | 日期选择器 |
| 手机号 | 登录手机号 | 只读展示（脱敏） |

**接口**: 复用 `PUT /api/v1/user/profile`。

**前端**:
- 完善 `pages/profile/index.vue`：展示+编辑个人信息
- 头像上传：调用 `PUT /api/v1/user/profile` 传 avatarUrl

---

### 2.3 用户通知系统（P0）

**现状**: 用户端有 `pages/notification/index.vue` 页面，但后端无通知表和通知接口。

#### 2.3.1 数据库设计

```sql
CREATE TABLE notifications (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    type VARCHAR(30) NOT NULL COMMENT '类型：SYSTEM/REVIEW/MEMBER/ORDER',
    biz_type VARCHAR(50) NULL DEFAULT NULL COMMENT '业务类型',
    biz_id VARCHAR(64) NULL DEFAULT NULL COMMENT '业务关联ID',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0=未读 1=已读',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    INDEX idx_user_id(user_id),
    INDEX idx_user_read(user_id, is_read),
    INDEX idx_type(type),
    INDEX idx_created_time(created_time)
) COMMENT '用户通知表';
```

#### 2.3.2 通知类型

| type | 触发场景 | 标题示例 | 内容示例 |
|------|----------|----------|----------|
| `SYSTEM` | 管理员发布公告 | 系统维护通知 | 系统将于今晚22:00进行维护... |
| `REVIEW` | 审核通过 | 消息审核通过 | 您的咨询消息已通过审核，AI已为您生成回复 |
| `REVIEW` | 审核违规 | 消息审核未通过 | 您的消息包含违规内容，已被标记 |
| `MEMBER` | 会员即将到期 | 会员即将到期 | 您的L2会员将于3天后到期... |
| `MEMBER` | 会员已到期 | 会员已到期 | 您的会员已到期，已降级为普通用户 |
| `ORDER` | 支付成功 | 支付成功 | 您已成功开通L1基础会员... |
| `ORDER` | 退款到账 | 退款到账 | 您的退款申请已通过，款项已退回... |

#### 2.3.3 接口设计

**用户端**:
```
GET    /api/v1/user/notifications              # 通知列表（分页）
GET    /api/v1/user/notifications/unread-count  # 未读数量（轮询/角标）
PUT    /api/v1/user/notifications/{id}/read     # 标记已读
PUT    /api/v1/user/notifications/read-all      # 全部已读
```

**管理端**:
```
GET    /api/v1/admin/notifications/list         # 通知管理列表
POST   /api/v1/admin/notifications/send         # 发送系统通知（公告）
DELETE /api/v1/admin/notifications/{id}         # 删除通知
```

#### 2.3.4 通知写入时机

| 场景 | 触发位置 | 通知类型 |
|------|----------|----------|
| 审核通过 | `MessageServiceImpl.reviewMessage()` | REVIEW |
| 审核违规 | `MessageServiceImpl.reviewMessage()` | REVIEW |
| 会员到期 | 定时任务（每日检查） | MEMBER |
| 支付成功 | `PaymentCallbackController` | ORDER |
| 退款到账 | `RefundServiceImpl.approveRefund()` | ORDER |
| 管理员公告 | `NotificationController.send()` | SYSTEM |

#### 2.3.5 前端

- 完善 `pages/notification/index.vue`：通知列表+未读角标
- 用户端首页/个人中心显示未读通知数（红点/角标）

---

### 2.4 管理员公告管理（P1）

**现状**: 无公告管理功能。

#### 2.4.1 方案

复用 `notifications` 表，管理员发送公告时批量写入通知记录：

```
管理员填写公告标题+内容+目标受众 → 点击发布
  → 后端查询目标用户列表
  → 批量插入 notifications 表（type=SYSTEM）
  → 用户端收到系统通知
```

#### 2.4.2 接口设计

```
GET    /api/v1/admin/announcements              # 公告列表
POST   /api/v1/admin/announcements              # 发布公告
PUT    /api/v1/admin/announcements/{id}         # 编辑公告
DELETE /api/v1/admin/announcements/{id}         # 删除公告
```

**发布公告请求体**:
```json
{
    "title": "系统维护通知",
    "content": "系统将于今晚22:00-23:00进行维护...",
    "targetType": "ALL",
    "targetLevels": ["L0", "L1", "L2", "L3"]
}
```

**targetType**:
- `ALL` — 全部用户
- `LEVEL` — 指定会员等级

#### 2.4.3 前端

- 管理端新建 `views/announcement/index.vue`：公告列表+发布弹窗
- 用户端 `pages/notification/index.vue` 展示系统通知

---

### 2.5 管理员账号管理（P1）

**现状**: 管理端有角色管理（`views/permission/roles.vue`）和权限管理（`views/permission/permissions.vue`），但没有独立的**管理员账号管理**页面。超级管理员无法直接创建/管理审核员和系统管理员账号。

#### 2.5.1 方案

新增管理员账号管理页面，仅超级管理员（R002）可访问：

| 功能 | 说明 |
|------|------|
| 管理员列表 | 展示所有管理员（R002/R003/R004），支持搜索 |
| 新建管理员 | 选择用户 + 分配角色 + 设置初始密码 |
| 编辑管理员 | 修改角色、状态 |
| 删除管理员 | 移除管理员角色（不删除用户） |
| 重置密码 | 重置管理员登录密码 |

#### 2.5.2 接口设计

```
GET    /api/v1/admin/admin-users                # 管理员列表
POST   /api/v1/admin/admin-users                # 创建管理员（分配角色）
PUT    /api/v1/admin/admin-users/{id}/role      # 修改管理员角色
PUT    /api/v1/admin/admin-users/{id}/status    # 启用/禁用
DELETE /api/v1/admin/admin-users/{id}           # 移除管理员角色
POST   /api/v1/admin/admin-users/{id}/reset-password  # 重置密码
```

#### 2.5.3 前端

- 管理端新建 `views/admin-user/index.vue`：管理员列表+操作
- 路由守卫：仅 R002 可访问

---

### 2.6 用户端支付页面清理（P1）

**现状**: 用户端有 `pages/payment/index.vue` 和 `pages/order/index.vue`，但作为健康咨询系统，用户端不应暴露完整的支付结算流程（应由运营人员线下处理或跳转第三方）。

#### 2.5.1 方案

| 页面 | 处理 |
|------|------|
| `pages/payment/index.vue` | 移除或改为"会员权益展示"页，不显示支付按钮 |
| `pages/order/index.vue` | 改为"我的会员"页，展示当前会员状态和到期时间 |
| `pages/member/index.vue` | 保留，展示会员等级权益对比，移除"立即开通"跳转支付逻辑 |

**原则**: 用户端只展示会员信息，不提供支付入口。开通会员由管理员在后台手动授予。

---

## 三、实施优先级与排期

### Phase 1（P0 — 核心体验）

| # | 功能 | 工作量 | 依赖 |
|---|------|--------|------|
| 1 | 用户注册引导页 | 小 | 无 |
| 2 | 用户个人资料编辑 | 小 | 无 |
| 3 | 通知系统（数据库+接口+前端） | 中 | 无 |

### Phase 2（P1 — 管理增强）

| # | 功能 | 工作量 | 依赖 |
|---|------|--------|------|
| 4 | 管理员公告管理 | 中 | 通知系统 |
| 5 | 管理员账号管理 | 中 | 无 |
| 6 | 用户端支付页面清理 | 小 | 无 |

---

## 四、数据库变更汇总

### 新增表

| 表名 | 说明 |
|------|------|
| `notifications` | 用户通知表 |
| `announcements` | 公告表（可选，也可复用 notifications） |

### 无变更表

现有表结构满足所有需求，无需修改。

---

## 五、接口变更汇总

### 新增接口

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 用户端 | `/api/v1/user/notifications` | GET | 通知列表 |
| 用户端 | `/api/v1/user/notifications/unread-count` | GET | 未读数量 |
| 用户端 | `/api/v1/user/notifications/{id}/read` | PUT | 标记已读 |
| 用户端 | `/api/v1/user/notifications/read-all` | PUT | 全部已读 |
| 管理端 | `/api/v1/admin/notifications/send` | POST | 发送系统通知 |
| 管理端 | `/api/v1/admin/announcements` | GET/POST/PUT/DELETE | 公告管理 |
| 管理端 | `/api/v1/admin/admin-users` | GET/POST | 管理员列表/创建 |
| 管理端 | `/api/v1/admin/admin-users/{id}/role` | PUT | 修改角色 |
| 管理端 | `/api/v1/admin/admin-users/{id}/status` | PUT | 启用/禁用 |
| 管理端 | `/api/v1/admin/admin-users/{id}/reset-password` | POST | 重置密码 |

### 修改接口

| 接口 | 变更 |
|------|------|
| `MessageServiceImpl.reviewMessage()` | 审核通过/违规时写入通知 |

---

## 六、风险与注意事项

| 风险 | 应对措施 |
|------|----------|
| 通知量大时查询性能 | `user_id + is_read` 联合索引，分页查询 |
| 批量发送公告时数据库压力 | 异步批量插入，分批处理 |
| 管理员误操作 | 删除管理员仅移除角色，不删除用户 |
| 会员到期检测遗漏 | 定时任务每日凌晨执行，失败重试 |

---

## 七、待确认事项

| # | 问题 | 建议 |
|---|------|------|
| 1 | 用户端是否保留会员购买入口？ | 建议移除，由管理员后台授予 |
| 2 | 公告是否需要定时发布？ | Phase 1 不需要，Phase 2 可考虑 |
| 3 | 通知是否需要推送（离线通知）？ | 当前方案为应用内通知，不涉及推送 |
| 4 | 管理员是否需要支持批量创建？ | 建议暂不支持，逐个创建 |
