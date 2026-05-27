---
name: "admin-dashboard"
description: "Admin dashboard and data visualization screen development guide for Neusoft Health Consulting system. Invoke when developing admin panel, data screens, or management features."
---

# 东软智慧健康咨询系统 - 管理员端开发指南

## 概述

本技能用于指导开发东软智慧健康咨询系统的管理员端功能，包括Web管理后台和数据可视化大屏。

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.4+ | 前端框架 |
| Element Plus | 2.7+ | UI组件库 |
| Vite | 5.x | 构建工具 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Axios | 1.7+ | HTTP请求 |
| ECharts | 5.5+ | 数据可视化 |
| TypeScript | 5.x | 类型系统 |

## 项目结构

```
neusoft-health-admin/
├── src/
│   ├── api/                    # API接口
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   │   ├── ProTable/           # 增强表格
│   │   ├── ProForm/            # 增强表单
│   │   └── Permission/         # 权限组件
│   ├── composables/            # 组合式函数
│   ├── directives/             # 自定义指令
│   ├── layouts/                # 布局组件
│   ├── router/                 # 路由配置
│   ├── stores/                 # 状态管理
│   ├── utils/                  # 工具函数
│   └── views/                  # 页面视图
│       ├── login/              # 登录页
│       ├── dashboard/          # 工作台
│       ├── user/               # 用户管理
│       ├── review/             # 内容审核
│       ├── member/             # 会员管理
│       ├── payment/            # 支付管理
│       ├── content/            # 内容管理
│       ├── config/             # 系统配置
│       ├── permission/         # 权限管理
│       ├── log/                # 系统日志
│       └── screen/             # 数据大屏
└── docs/                       # 文档
```

## 功能模块

### 1. 工作台 (Dashboard)

管理员登录后的默认页面，展示系统关键指标和待办事项。

**核心指标卡片**：
- 累计用户数
- 今日新增用户
- 活跃用户数
- 付费用户数
- 今日咨询量
- AI平均响应时间

**图表组件**：
- 用户增长趋势图（面积图）
- 咨询量趋势图（折线图）
- 会员等级分布（玫瑰图）
- 待办事项列表

### 2. 用户管理

**功能**：
- 用户列表（分页、筛选、排序）
- 用户详情查看
- 用户状态切换（启用/禁用）
- 角色分配

**API接口**：
```
GET  /api/v1/admin/users           # 用户列表
GET  /api/v1/admin/users/{id}      # 用户详情
PUT  /api/v1/admin/users/{id}/status  # 状态切换
PUT  /api/v1/admin/user-roles/assign  # 角色分配
```

### 3. 内容审核

**审核流程**：
```
用户发送消息 → AI初审 → 人工复审 → 通过/违规
```

**API接口**：
```
GET  /api/v1/admin/review/pending   # 待审核列表
POST /api/v1/admin/review/message   # 审核操作
```

### 4. 会员管理

**功能**：
- 会员列表查看
- 手动开通/撤销会员
- 会员统计分析

**API接口**：
```
GET  /api/v1/admin/member/users     # 会员列表
GET  /api/v1/admin/member/stats     # 会员统计
POST /api/v1/admin/member/grant     # 手动开通
POST /api/v1/admin/member/revoke    # 撤销会员
```

### 5. 支付管理

**功能**：
- 订单列表查看
- 退款审核处理

**API接口**：
```
GET  /api/v1/admin/payment/orders   # 订单列表
GET  /api/v1/admin/payment/refunds  # 退款列表
POST /api/v1/admin/payment/refund/{id}/approve  # 批准退款
POST /api/v1/admin/payment/refund/{id}/reject   # 拒绝退款
```

### 6. 内容管理

**FAQ管理**：
```
GET    /api/v1/admin/faq/list       # FAQ列表
POST   /api/v1/admin/faq/create     # 新增FAQ
PUT    /api/v1/admin/faq/{id}       # 编辑FAQ
DELETE /api/v1/admin/faq/{id}       # 删除FAQ
```

**敏感词管理**：
```
GET    /api/v1/admin/sensitive-word          # 敏感词列表
POST   /api/v1/admin/sensitive-word          # 新增敏感词
PUT    /api/v1/admin/sensitive-word/{id}     # 编辑敏感词
DELETE /api/v1/admin/sensitive-word/{id}     # 删除敏感词
```

### 7. 系统配置

**配置项**：
- 基础配置（系统名称、Logo）
- AI配置（API Key、模型参数）
- 短信配置
- 支付配置
- 会员配置

**API接口**：
```
GET /api/v1/admin/config/{configKey}    # 获取配置
PUT /api/v1/admin/config                # 更新配置
```

### 8. 权限管理

**预置角色**：
| 角色编码 | 角色名称 | 权限范围 |
|----------|----------|----------|
| R001 | 普通用户 | C端用户，无管理权限 |
| R002 | 超级管理员 | 全部权限 |
| R003 | 系统管理员 | 用户管理、配置、统计、日志 |
| R004 | 内容审核员 | 内容审核、FAQ管理 |

**权限码设计**：
```
模块:操作:资源

示例：
user:list        - 用户列表
user:create      - 创建用户
user:edit        - 编辑用户
user:delete      - 删除用户
content:review   - 内容审核
member:list      - 会员列表
payment:list     - 订单列表
config:edit      - 编辑配置
role:list        - 角色列表
log:view         - 查看日志
```

**API接口**：
```
GET  /api/v1/admin/roles                # 角色列表
POST /api/v1/admin/roles                # 新增角色
PUT  /api/v1/admin/roles/{id}           # 编辑角色
DELETE /api/v1/admin/roles/{id}         # 删除角色
PUT  /api/v1/admin/roles/{id}/permissions  # 分配权限

GET  /api/v1/admin/permissions          # 权限列表
GET  /api/v1/admin/permissions/tree     # 权限树
POST /api/v1/admin/permissions          # 新增权限
PUT  /api/v1/admin/permissions/{id}     # 编辑权限
DELETE /api/v1/admin/permissions/{id}   # 删除权限
```

### 9. 系统监控

**操作日志**：
```
GET /api/v1/admin/logs?page=1&size=10&action=LOGIN
```

**登录日志**：
```
GET /api/v1/user/login-logs?page=1&size=10
```

## 数据可视化大屏

### 布局设计

采用经典布局，1920x1080分辨率设计：

```
┌──────────────────────────────────────────────────────────────────────┐
│  [Logo]  东软智慧健康 · 数据运营中心          2026-05-25 15:30:00    │
├──────────┬────────────┬──────────┬──────────┬─────────────┬──────────┤
│ 累计用户  │  今日新增   │  活跃用户  │  付费用户  │  会员转化率   │  AI响应   │
├──────────┴────────────┴──────────┴──────────┴─────────────┴──────────┤
│                  [用户增长趋势图]          [咨询量趋势图]              │
├──────────────────────────────────────────────────────────────────────┤
│  [会员等级分布]  [实时咨询滚动]          [紧急情况漏斗图]              │
└──────────────────────────────────────────────────────────────────────┘
```

### 深色主题配色

| 用途 | 色值 |
|------|------|
| 背景色 | `#0A0E27` |
| 卡片背景 | `rgba(6, 30, 93, 0.5)` |
| 边框 | `rgba(0, 150, 255, 0.3)` |
| 主色调 | `#00D4FF` |
| 辅助色 | `#00FF88` |
| 警告色 | `#FF6B6B` |
| 文字色 | `#E0E6FF` |

### ECharts图表配置

**用户增长趋势图**：
```javascript
{
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: dates },
  yAxis: { type: 'value' },
  series: [{
    type: 'line',
    smooth: true,
    areaStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(0, 212, 255, 0.4)' },
        { offset: 1, color: 'rgba(0, 212, 255, 0.02)' }
      ])
    }
  }]
}
```

**会员等级玫瑰图**：
```javascript
{
  series: [{
    type: 'pie',
    radius: ['30%', '70%'],
    roseType: 'area',
    data: [
      { value: 80000, name: 'L0 普通用户', itemStyle: { color: '#6B7280' } },
      { value: 30000, name: 'L1 基础会员', itemStyle: { color: '#3B82F6' } },
      { value: 12000, name: 'L2 高级会员', itemStyle: { color: '#F59E0B' } },
      { value: 6456, name: 'L3 专业会员', itemStyle: { color: '#EAB308' } }
    ]
  }]
}
```

### 数据刷新策略

| 数据区域 | 刷新频率 |
|----------|----------|
| 核心指标卡片 | 10-30秒 |
| 咨询实时滚动 | 实时推送 |
| 趋势图表 | 60秒 |
| 分布图表 | 120秒 |
| 待办事项 | 30秒 |

### 屏幕适配

```typescript
function handleResize() {
  const scaleX = window.innerWidth / 1920;
  const scaleY = window.innerHeight / 1080;
  const scale = Math.min(scaleX, scaleY);
  document.getElementById('app').style.transform = `scale(${scale})`;
  document.getElementById('app').style.transformOrigin = 'top left';
}

window.addEventListener('resize', handleResize);
handleResize();
```

## 组件开发规范

### ProTable 组件

增强表格组件，支持筛选、排序、分页。

```vue
<template>
  <pro-table
    :columns="columns"
    :request="fetchData"
    :pagination="true"
  >
    <template #toolbar>
      <el-button type="primary" @click="handleCreate">新增</el-button>
    </template>
    <template #actions="{ row }">
      <el-button @click="handleEdit(row)">编辑</el-button>
      <el-button type="danger" @click="handleDelete(row)">删除</el-button>
    </template>
  </pro-table>
</template>
```

### 权限指令

```typescript
// 按钮级权限指令
v-permission="'user:create'"
v-permission="'user:delete'"

// 角色权限
v-role="'R002'"
v-role="['R002', 'R003']"
```

### Axios封装

```typescript
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
});

service.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

service.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data;
    if (code === 200) {
      return data;
    }
    ElMessage.error(message);
    return Promise.reject(new Error(message));
  },
  (error) => {
    if (error.response?.status === 401) {
      removeToken();
      router.push('/login');
    }
    return Promise.reject(error);
  }
);
```

## 路由配置

```typescript
const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'users',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', permission: 'user:list' }
      },
      {
        path: 'review',
        component: () => import('@/views/review/index.vue'),
        meta: { title: '内容审核', permission: 'content:review' }
      },
      {
        path: 'members',
        component: () => import('@/views/member/index.vue'),
        meta: { title: '会员管理', permission: 'member:list' }
      },
      {
        path: 'orders',
        component: () => import('@/views/payment/orders.vue'),
        meta: { title: '订单管理', permission: 'payment:list' }
      },
      {
        path: 'screen',
        component: () => import('@/views/screen/index.vue'),
        meta: { title: '数据大屏', fullscreen: true }
      }
    ]
  }
];
```

## 状态管理

```typescript
// stores/auth.ts
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    user: null,
    permissions: []
  }),
  actions: {
    async login(credentials) {
      const { token } = await loginApi(credentials);
      this.token = token;
      setToken(token);
    },
    async getUserInfo() {
      this.user = await getUserInfoApi();
    },
    async getPermissions() {
      this.permissions = await getPermissionsApi();
    },
    hasPermission(perm) {
      return this.permissions.includes(perm);
    },
    logout() {
      this.token = '';
      this.user = null;
      this.permissions = [];
      removeToken();
    }
  }
});
```

## 路由守卫

```typescript
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  const whiteList = ['/login'];

  if (authStore.token) {
    if (to.path === '/login') {
      next({ path: '/' });
    } else {
      if (!authStore.user) {
        await authStore.getUserInfo();
        await authStore.getPermissions();
      }
      next();
    }
  } else {
    if (whiteList.includes(to.path)) {
      next();
    } else {
      next(`/login?redirect=${to.path}`);
    }
  }
});
```

## API响应格式

```typescript
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface PageResponse<T> {
  total: number;
  list: T[];
  page: number;
  pageSize: number;
}
```

## 开发注意事项

1. **权限控制**：所有管理端接口必须验证用户角色（R002/R003/R004）
2. **数据脱敏**：手机号、身份证号等敏感信息在前端展示时需脱敏
3. **错误处理**：统一使用ElMessage显示错误信息
4. **加载状态**：所有异步操作需显示loading状态
5. **表单验证**：使用Element Plus的表单验证功能
6. **响应式设计**：管理后台需适配不同屏幕尺寸

## 示例文件

- 数据大屏示例：`examples/dashboard-screen.html`
- 设计文档：`docs/admin-design.md`