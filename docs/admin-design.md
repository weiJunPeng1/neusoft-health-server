# 东软智慧健康咨询系统 - 管理员端设计文档

## 一、概述

### 1.1 项目背景

东软智慧健康咨询系统是一个 AI 驱动的健康咨询平台，包含 C 端用户应用（UniApp）和管理后台。本文档定义管理员端的完整设计方案，用于系统运营管理。

### 1.2 设计目标

- **多端适配**：支持 Web 管理后台（PC）、桌面端（Electron）、移动端管理（UniApp）
- **权限精细**：基于 RBAC 的细粒度权限控制
- **数据可视化**：直观的统计图表和数据看板
- **高效运维**：批量操作、快捷搜索、实时通知

---

## 二、技术选型

### 2.1 推荐方案：Web 管理后台（Vue 3 + Element Plus）

| 技术 | 选型 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue 3 | 3.4+ | Composition API |
| UI 组件库 | Element Plus | 2.7+ | 企业级组件库 |
| 构建工具 | Vite | 5.x | 快速构建 |
| 路由 | Vue Router | 4.x | 路由管理 |
| 状态管理 | Pinia | 2.x | 轻量级状态管理 |
| HTTP 客户端 | Axios | 1.7+ | 请求拦截、Token 刷新 |
| 图表 | ECharts | 5.5+ | 数据可视化 |
| 权限 | 自定义指令 | - | 按钮级权限控制 |

### 2.2 备选方案对比

| 方案 | 技术栈 | 优势 | 劣势 | 适用场景 |
|------|--------|------|------|----------|
| **Web 管理后台** | Vue 3 + Element Plus | 开发效率高、跨平台、易维护 | 需要浏览器 | 推荐，适合大多数管理场景 |
| 桌面端 | Electron + Vue 3 | 离线可用、系统集成 | 包体积大、性能一般 | 需要离线或系统级集成 |
| 移动管理端 | UniApp + uView | 随时随地管理 | 屏幕限制、功能精简 | 巡检、审批等轻量场景 |
| Android 原生 | Kotlin + Jetpack Compose | 性能好、体验佳 | 开发成本高 | 特定 Android 管理需求 |

---

## 三、功能模块设计

### 3.1 功能架构图

```
管理员端
├── 工作台（Dashboard）
│   ├── 数据概览
│   ├── 待办事项
│   └── 快捷操作
├── 用户管理
│   ├── 用户列表
│   ├── 用户详情
│   ├── 角色分配
│   └── 账号禁用/启用
├── 内容审核
│   ├── 待审核消息
│   ├── 审核历史
│   └── 违规处理
├── 会员管理
│   ├── 会员列表
│   ├── 会员统计
│   ├── 手动开通/撤销
│   └── 会员等级配置
├── 支付管理
│   ├── 订单列表
│   ├── 退款审核
│   └── 支付统计
├── 内容管理
│   ├── FAQ 管理
│   ├── FAQ 分类
│   ├── 健康资讯
│   └── 敏感词管理
├── 系统配置
│   ├── 基础配置
│   ├── AI 配置
│   ├── 短信配置
│   └── 支付配置
├── 权限管理
│   ├── 角色管理
│   ├── 权限管理
│   └── 用户角色分配
└── 系统监控
    ├── 操作日志
    ├── 登录日志
    └── 系统状态
```

### 3.2 功能模块详细设计

#### 3.2.1 工作台（Dashboard）

**功能描述**：管理员登录后的默认页面，展示系统关键指标和待办事项。

**数据指标**：

| 指标 | 说明 | API |
|------|------|-----|
| 今日新增用户 | 当天注册用户数 | `GET /api/v1/admin/stats/dashboard` |
| 活跃用户数 | 今日登录用户数 | 同上 |
| 今日咨询量 | 当天咨询会话数 | 同上 |
| 待审核消息 | 需要审核的消息数 | `GET /api/v1/admin/review/pending` |
| 待处理退款 | 需要审核的退款申请 | `GET /api/v1/admin/payment/refunds` |
| 会员转化率 | 付费用户占比 | `GET /api/v1/admin/member/stats` |

**图表设计**：

1. **趋势图**：近 7 天/30 天用户增长、咨询量趋势（折线图）
2. **分布图**：会员等级分布（饼图）、咨询类型分布（柱状图）
3. **实时数据**：在线用户数、AI 响应时间（数字卡片）

**页面布局**：

```
┌─────────────────────────────────────────────────────────┐
│  [数据卡片1] [数据卡片2] [数据卡片3] [数据卡片4]          │
├─────────────────────────────────────────────────────────┤
│  [趋势图 - 用户增长]          [分布图 - 会员等级]         │
├─────────────────────────────────────────────────────────┤
│  [待办事项列表]               [最近操作日志]              │
└─────────────────────────────────────────────────────────┘
```

---

#### 3.2.2 用户管理

**功能描述**：管理系统用户，包括查看、编辑、禁用、角色分配。

**列表字段**：

| 字段 | 说明 | 筛选 | 排序 |
|------|------|------|------|
| ID | 用户ID | - | ✅ |
| 昵称 | 用户昵称 | ✅ 模糊搜索 | - |
| 手机号 | 脱敏显示 | ✅ 模糊搜索 | - |
| 会员等级 | L0/L1/L2/L3 | ✅ 下拉筛选 | - |
| 状态 | 正常/禁用 | ✅ 下拉筛选 | - |
| 注册时间 | 创建时间 | ✅ 日期范围 | ✅ |
| 最后登录 | 最后登录时间 | - | ✅ |

**操作按钮**：

| 按钮 | 权限 | 说明 |
|------|------|------|
| 查看 | R002/R003 | 查看用户详情 |
| 编辑 | R002/R003 | 编辑用户信息 |
| 禁用/启用 | R002/R003 | 切换用户状态 |
| 分配角色 | R002 | 分配用户角色 |
| 删除 | R002 | 删除用户（软删除） |

**API 对接**：

```
列表: GET /api/v1/admin/users?page=1&size=10&keyword=xxx
详情: GET /api/v1/admin/users/{id}
状态: PUT /api/v1/admin/users/{id}/status
删除: DELETE /api/v1/admin/users/{id}
角色: PUT /api/v1/admin/user-roles/assign
```

---

#### 3.2.3 内容审核

**功能描述**：审核用户发送的消息，识别违规内容。

**审核流程**：

```
用户发送消息 → AI 初审（敏感词/紧急词） → 人工复审 → 通过/违规
```

**列表字段**：

| 字段 | 说明 |
|------|------|
| 消息ID | 消息唯一标识 |
| 用户 | 发送用户（昵称/手机） |
| 内容 | 消息内容（可展开查看完整） |
| 会话 | 所属会话 |
| AI 判断 | 紧急/违规/正常 |
| 发送时间 | 消息创建时间 |
| 状态 | 待审/已通过/违规 |

**审核操作**：

| 操作 | 说明 |
|------|------|
| 通过 | 标记消息为正常 |
| 违规 | 标记消息为违规，可填写违规原因 |
| 修改 | 修改消息内容（脱敏处理） |
| 查看查看会话 | 查看完整会话上下文 |

**API 对接**：

```
列表: GET /api/v1/admin/review/pending?page=1&size=10
审核: POST /api/v1/admin/review/message { messageId, status, reason }
```

---

#### 3.2.4 会员管理

**功能描述**：管理会员用户，包括查看、手动开通、撤销、统计。

**会员列表**：

| 字段 | 说明 |
|------|------|
| 用户 | 昵称/手机号 |
| 会员等级 | L1/L2/L3 |
| 开通时间 | 会员开始时间 |
| 到期时间 | 会员过期时间 |
| 来源 | 自然开通/手动开通/邀请 |
| 状态 | 有效/过期/宽限期 |

**操作按钮**：

| 按钮 | 权限 | 说明 |
|------|------|------|
| 手动开通 | R002 | 为用户开通会员 |
| 撤销会员 | R002 | 撤销用户会员资格 |
| 续期 | R002 | 延长会员有效期 |

**统计数据**：

| 指标 | 说明 |
|------|------|
| 总会员数 | 各等级会员数量 |
| 今日新增 | 今日新开通会员 |
| 即将到期 | 7 天内到期会员 |
| 转化率 | 注册到付费转化率 |

**API 对联**：

```
列表: GET /api/v1/admin/member/users?page=1&size=10&level=L1
统计: GET /api/v1/admin/member/stats
开通: POST /api/v1/admin/member/grant { userId, levelCode, days }
撤销: POST /api/v1/admin/member/revoke { userId }
```

---

#### 3.2.5 支付管理

**功能描述**：管理支付订单和退款申请。

**订单列表**：

| 字段 | 说明 |
|------|------|
| 订单号 | 唯一标识 |
| 用户 | 下单用户 |
| 方案 | 订阅方案名称 |
| 金额 | 支付金额 |
| 状态 | 待支付/已支付/已取消/退款中/已退款 |
| 支付方式 | 支付宝/微信 |
| 创建时间 | 下单时间 |
| 支付时间 | 支付完成时间 |

**退款审核**：

| 字段 | 说明 |
|------|------|
| 退款ID | 退款申请ID |
| 订单号 | 原订单号 |
| 用户 | 申请用户 |
| 退款金额 | 申请退款金额 |
| 原因 | 退款原因 |
| 状态 | 待审核/已通过/已拒绝/已到账 |
| 申请时间 | 申请时间 |

**审核操作**：

| 操作 | 权限 | 说明 |
|------|------|------|
| 批准 | R002 | 批准退款申请 |
| 拒绝 | R002 | 拒绝退款申请，需填写原因 |

**API 对接**：

```
订单列表: GET /api/v1/admin/payment/orders?page=1&size=10&status=1
退款列表: GET /api/v1/admin/payment/refunds?page=1&size=10&status=0
批准退款: POST /api/v1/admin/payment/refund/{id}/approve { remark }
拒绝退款: POST /api/v1/admin/payment/refund/{id}/reject { remark }
```

---

#### 3.2.6 内容管理

**功能描述**：管理 FAQ、健康资讯、敏感词等内容。

**FAQ 管理**：

| 功能 | 说明 |
|------|------|
| 分类管理 | 新增、编辑、删除 FAQ 分类 |
| FAQ 管理 | 新增、编辑、删除 FAQ 条目 |
| 排序 | 拖拽排序 |
| 预览 | 预览 FAQ 展示效果 |

**敏感词管理**：

| 功能 | 说明 |
|------|------|
| 词库管理 | 新增、编辑、删除敏感词 |
| 分类 | 政治/暴力/色情/医疗风险/违法 |
| 严重级别 | 普通/高风险 |
| 批量导入 | 支持批量导入敏感词 |

**API 对接**：

```
FAQ列表: GET /api/v1/admin/faq/list?page=1&size=10
新增FAQ: POST /api/v1/admin/faq/create { question, answer, categoryId }
编辑FAQ: PUT /api/v1/admin/faq/{id} { question, answer, categoryId }
删除FAQ: DELETE /api/v1/admin/faq/{id}

敏感词列表: GET /api/v1/admin/sensitive-word?page=1&size=10
新增敏感词: POST /api/v1/admin/sensitive-word { word, category, severity }
编辑敏感词: PUT /api/v1/admin/sensitive-word/{id} { word, category, severity }
删除敏感词: DELETE /api/v1/admin/sensitive-word/{id}
```

---

#### 3.2.7 系统配置

**功能描述**：管理系统全局配置参数。

**配置分类**：

| 分类 | 配置项 | 说明 |
|------|--------|------|
| 基础配置 | 系统名称、Logo、版权信息 | 系统基础信息 |
| AI 配置 | DeepSeek API Key、模型、温度 | AI 咨询相关配置 |
| 短信配置 | 短信服务商、模板 | 短信发送配置 |
| 支付配置 | 支付宝 AppID、密钥 | 支付相关配置 |
| 会员配置 | 等级权益、配额 | 会员体系配置 |

**API 对接**：

```
获取配置: GET /api/v1/admin/config/{configKey}
更新配置: PUT /api/v1/admin/config { configKey, configValue }
```

---

#### 3.2.8 权限管理

**功能描述**：管理角色、权限、用户角色分配。

**角色管理**：

| 功能 | 权限 | 说明 |
|------|------|------|
| 角色列表 | R002/R003 | 查看所有角色 |
| 新增角色 | R002 | 创建新角色 |
| 编辑角色 | R002 | 修改角色信息 |
| 删除角色 | R002 | 删除角色 |
| 分配权限 | R002 | 为角色分配权限 |

**权限管理**：

| 功能 | 权限 | 说明 |
|------|------|------|
| 权限列表 | R002/R003 | 查看所有权限 |
| 权限树 | R002/R003 | 树形展示权限 |
| 新增权限 | R002 | 创建新权限 |
| 编辑权限 | R002 | 修改权限信息 |
| 删除权限 | R002 | 删除权限 |

**预置角色**：

| 角色编码 | 角色名称 | 权限范围 |
|----------|----------|----------|
| R001 | 普通用户 | C 端用户，无管理权限 |
| R002 | 超级管理员 | 全部权限 |
| R003 | 系统管理员 | 用户管理、配置、统计、日志 |
| R004 | 内容审核员 | 内容审核、FAQ 管理 |

**API 对接**：

```
角色列表: GET /api/v1/admin/roles
角色详情: GET /api/v1/admin/roles/{id}
新增角色: POST /api/v1/admin/roles { roleName, description }
编辑角色: PUT /api/v1/admin/roles/{id} { roleName, description }
删除角色: DELETE /api/v1/admin/roles/{id}
分配权限: PUT /api/v1/admin/roles/{id}/permissions { permissionIds }

权限列表: GET /api/v1/admin/permissions
权限树: GET /api/v1/admin/permissions/tree
新增权限: POST /api/v1/admin/permissions { permCode, permName, permType, parentId }
编辑权限: PUT /api/v1/admin/permissions/{id} { permName, permType }
删除权限: DELETE /api/v1/admin/permissions/{id}

用户角色: GET /api/v1/admin/user-roles/{userId}
分配角色: PUT /api/v1/admin/user-roles/assign { userId, roleIds }
```

---

#### 3.2.9 系统监控

**功能描述**：查看系统操作日志、登录日志、系统状态。

**操作日志**：

| 字段 | 说明 |
|------|------|
| 操作人 | 操作人员 |
| 操作类型 | 登录/新增/编辑/删除/审核等 |
| 操作模块 | 用户/内容/配置等 |
| 操作详情 | 具体操作内容 |
| IP 地址 | 操作 IP |
| 操作时间 | 操作时间 |

**登录日志**：

| 字段 | 说明 |
|------|------|
| 用户 | 登录用户 |
| 登录方式 | 短信/密码 |
| 登录结果 | 成功/失败 |
| IP 地址 | 登录 IP |
| 设备信息 | 浏览器/系统 |
| 登录时间 | 登录时间 |

**API 对接**：

```
操作日志: GET /api/v1/admin/logs?page=1&size=10&action=LOGIN
登录日志: GET /api/v1/user/login-logs?page=1&size=10
```

---

## 四、页面设计规范

### 4.1 布局结构

```
┌─────────────────────────────────────────────────────────┐
│  [Logo] [系统名称]              [用户头像] [用户名] [退出] │
├────────┬────────────────────────────────────────────────┤
│        │  [面包屑导航]                                   │
│  侧边栏 ├────────────────────────────────────────────────┤
│  菜单   │                                                │
│        │  [页面内容区域]                                  │
│        │                                                │
│        │                                                │
│        │                                                │
│        ├────────────────────────────────────────────────┤
│        │  [分页/操作栏]                                   │
└────────┴────────────────────────────────────────────────┘
```

### 4.2 页面清单

| 页面 | 路由 | 说明 |
|------|------|------|
| 登录页 | `/login` | 管理员登录 |
| 工作台 | `/dashboard` | 数据概览 |
| 用户列表 | `/users` | 用户管理 |
| 用户详情 | `/users/:id` | 用户详情 |
| 内容审核 | `/review` | 消息审核 |
| 会员列表 | `/members` | 会员管理 |
| 订单列表 | `/orders` | 订单管理 |
| 退款审核 | `/refunds` | 退款管理 |
| FAQ 管理 | `/faq` | FAQ 管理 |
| FAQ 分类 | `/faq/categories` | FAQ 分类 |
| 敏感词 | `/sensitive-words` | 敏感词管理 |
| 系统配置 | `/config` | 系统配置 |
| 角色管理 | `/roles` | 角色管理 |
| 权限管理 | `/permissions` | 权限管理 |
| 操作日志 | `/logs` | 操作日志 |

### 4.3 组件设计

#### 4.3.1 公共组件

| 组件 | 说明 | 用途 |
|------|------|------|
| `ProTable` | 增强表格 | 列表页通用表格，支持筛选、排序、分页 |
| `ProForm` | 增强表单 | 新增/编辑表单，支持验证、联动 |
| `ProModal` | 增强弹窗 | 确认弹窗、表单弹窗 |
| `SearchBar` | 搜索栏 | 关键词搜索、筛选条件 |
| `StatusTag` | 状态标签 | 状态展示（不同颜色） |
| `Permission` | 权限组件 | 按钮级权限控制 |
| `Chart` | 图表组件 | ECharts 封装 |

#### 4.3.2 权限指令

```typescript
// 按钮级权限指令
v-permission="'user:create'"
v-permission="'user:delete'"

// 角色权限
v-role="'R002'"
v-role="['R002', 'R003']"
```

---

## 五、权限设计

### 5.1 权限码设计

```
模块:操作:资源

示例：
user:list        - 用户列表
user:create      - 创建用户
user:edit        - 编辑用户
user:delete      - 删除用户
user:status      - 修改用户状态

content:review   - 内容审核
content:faq      - FAQ 管理

member:list      - 会员列表
member:grant     - 手动开通
member:revoke    - 撤销会员

payment:list     - 订单列表
payment:refund   - 退款审核

config:view      - 查看配置
config:edit      - 编辑配置

role:list        - 角色列表
role:create      - 创建角色
role:edit        - 编辑角色
role:delete      - 删除角色
role:permission  - 分配权限

log:view         - 查看日志
```

### 5.2 角色权限矩阵

| 功能模块 | R002 超级管理员 | R003 系统管理员 | R004 内容审核员 |
|----------|-----------------|-----------------|-----------------|
| 工作台 | ✅ | ✅ | ✅ |
| 用户管理 | ✅ | ✅ | ❌ |
| 内容审核 | ✅ | ❌ | ✅ |
| 会员管理 | ✅ | ✅ | ❌ |
| 支付管理 | ✅ | ✅ | ❌ |
| FAQ 管理 | ✅ | ❌ | ✅ |
| 敏感词管理 | ✅ | ✅ | ❌ |
| 系统配置 | ✅ | ✅ | ❌ |
| 权限管理 | ✅ | ❌ | ❌ |
| 操作日志 | ✅ | ✅ | ❌ |

---

## 六、数据流设计

### 6.1 登录流程

```
管理员输入账号密码
  → POST /api/v1/auth/login/password { phone, password }
  → 后端验证：检查用户存在 → 检查密码 → 检查角色（R002/R003/R004）
  → 返回 JWT Token
  → 前端存储 Token，跳转工作台
```

### 6.2 权限验证流程

```
页面加载 → 获取用户信息和权限列表
  → 根据权限动态生成菜单
  → 按钮级权限：v-permission 指令检查
  → API 请求：自动附加 Token
  → 后端验证：JwtAuthenticationFilter → SecurityConfig 规则校验
```

### 6.3 数据请求流程

```
页面发起请求
  → Axios 拦截器：附加 Token、处理错误
  → 后端处理：参数校验 → 业务逻辑 → 返回结果
  → 前端处理：loading 状态、错误提示、数据渲染
```

---

## 七、项目结构

### 7.1 推荐目录结构

```
neusoft-health-admin/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                    # API 接口
│   │   ├── auth.ts             # 认证接口
│   │   ├── user.ts             # 用户接口
│   │   ├── review.ts           # 审核接口
│   │   ├── member.ts           # 会员接口
│   │   ├── payment.ts          # 支付接口
│   │   ├── faq.ts              # FAQ 接口
│   │   ├── sensitive.ts        # 敏感词接口
│   │   ├── config.ts           # 配置接口
│   │   ├── role.ts             # 角色接口
│   │   ├── permission.ts       # 权限接口
│   │   └── log.ts              # 日志接口
│   ├── assets/                 # 静态资源
│   │   ├── images/
│   │   └── styles/
│   ├── components/             # 公共组件
│   │   ├── ProTable/
│   │   ├── ProForm/
│   │   ├── ProModal/
│   │   ├── SearchBar/
│   │   ├── StatusTag/
│   │   └── Permission/
│   ├── composables/            # 组合式函数
│   │   ├── useAuth.ts          # 认证相关
│   │   ├── usePermission.ts    # 权限相关
│   │   └── useTable.ts         # 表格相关
│   ├── directives/             # 自定义指令
│   │   └── permission.ts       # 权限指令
│   ├── layouts/                # 布局组件
│   │   ├── AdminLayout.vue     # 管理后台布局
│   │   ├── Sidebar.vue         # 侧边栏
│   │   └── Header.vue          # 头部
│   ├── router/                 # 路由配置
│   │   ├── index.ts            # 路由入口
│   │   ├── modules/            # 路由模块
│   │   │   ├── dashboard.ts
│   │   │   ├── user.ts
│   │   │   ├── review.ts
│   │   │   ├── member.ts
│   │   │   ├── payment.ts
│   │   │   ├── content.ts
│   │   │   ├── config.ts
│   │   │   ├── permission.ts
│   │   │   └── log.ts
│   │   └── guards.ts           # 路由守卫
│   ├── stores/                 # 状态管理
│   │   ├── auth.ts             # 认证状态
│   │   ├── permission.ts       # 权限状态
│   │   └── app.ts              # 应用状态
│   ├── utils/                  # 工具函数
│   │   ├── request.ts          # Axios 封装
│   │   ├── auth.ts             # Token 管理
│   │   └── permission.ts       # 权限工具
│   ├── views/                  # 页面视图
│   │   ├── login/
│   │   │   └── index.vue
│   │   ├── dashboard/
│   │   │   └── index.vue
│   │   ├── user/
│   │   │   ├── index.vue       # 用户列表
│   │   │   └── detail.vue      # 用户详情
│   │   ├── review/
│   │   │   └── index.vue       # 内容审核
│   │   ├── member/
│   │   │   └── index.vue       # 会员管理
│   │   ├── payment/
│   │   │   ├── orders.vue      # 订单列表
│   │   │   └── refunds.vue     # 退款审核
│   │   ├── content/
│   │   │   ├── faq.vue         # FAQ 管理
│   │   │   ├── faq-category.vue # FAQ 分类
│   │   │   └── sensitive.vue   # 敏感词
│   │   ├── config/
│   │   │   └── index.vue       # 系统配置
│   │   ├── permission/
│   │   │   ├── roles.vue       # 角色管理
│   │   │   └── permissions.vue # 权限管理
│   │   └── log/
│   │       └── index.vue       # 操作日志
│   ├── App.vue
│   └── main.ts
├── .env                        # 环境变量
├── .env.development            # 开发环境
├── .env.production             # 生产环境
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

### 7.2 关键配置

#### 7.2.1 环境变量

```properties
# .env.development
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=东软健康管理系统

# .env.production
VITE_API_BASE_URL=https://api.neusoft-health.com
VITE_APP_TITLE=东软健康管理系统
```

#### 7.2.2 路由守卫

```typescript
// router/guards.ts
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 白名单路由
  const whiteList = ['/login']
  
  if (authStore.token) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      // 检查是否已获取用户信息
      if (!authStore.user) {
        await authStore.getUserInfo()
        await authStore.getPermissions()
      }
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})
```

#### 7.2.3 Axios 封装

```typescript
// utils/request.ts
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token 过期，跳转登录
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

---

## 八、开发计划

### 8.1 开发阶段

| 阶段 | 内容 | 时间 |
|------|------|------|
| 第一阶段 | 项目初始化、登录、布局、工作台 | 1 周 |
| 第二阶段 | 用户管理、内容审核 | 1 周 |
| 第三阶段 | 会员管理、支付管理 | 1 周 |
| 第四阶段 | 内容管理、系统配置 | 1 周 |
| 第五阶段 | 权限管理、系统监控 | 1 周 |
| 第六阶段 | 测试、优化、部署 | 1 周 |

### 8.2 技术风险

| 风险 | 应对方案 |
|------|----------|
| 权限控制复杂 | 使用成熟的 RBAC 方案，权限码规范设计 |
| 数据量大 | 分页加载、虚拟滚动、数据缓存 |
| 接口联调 | 统一 API 封装，错误处理标准化 |

---

## 九、部署方案

### 9.1 开发环境

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建测试版本
npm run build:test
```

### 9.2 生产环境

```bash
# 构建生产版本
npm run build

# 部署到 Nginx
server {
    listen 80;
    server_name admin.neusoft-health.com;
    
    location / {
        root /var/www/neusoft-health-admin;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 十、附录

### 10.1 API 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token 无效或过期） |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 10.2 状态码定义

**用户状态**：

| 状态码 | 说明 |
|--------|------|
| 0 | 禁用 |
| 1 | 正常 |

**会员等级**：

| 等级码 | 说明 |
|--------|------|
| L0 | 普通用户（免费） |
| L1 | 基础会员 |
| L2 | 高级会员 |
| L3 | 专业会员 |

**订单状态**：

| 状态码 | 说明 |
|--------|------|
| 0 | 待支付 |
| 1 | 已支付 |
| 2 | 已取消 |
| 3 | 退款中 |
| 4 | 已到账（退款成功） |
| 5 | 到账失败（退款失败） |

**退款状态**：

| 状态码 | 说明 |
|--------|------|
| 0 | 待审核 |
| 1 | 已通过 |
| 2 | 已拒绝 |
| 3 | 已到账 |
| 4 | 到账失败 |

**审核状态**：

| 状态码 | 说明 |
|--------|------|
| 0 | 待审核 |
| 1 | 已通过 |
| 2 | 违规 |
