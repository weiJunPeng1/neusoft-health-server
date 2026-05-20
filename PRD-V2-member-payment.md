# 东软智慧健康咨询系统 V2.0 — 会员与付费模块 PRD

**文档版本**：V1.1（评审修订版）
**编写日期**：2026-05-19
**支付渠道**：支付宝（AI收 + 个人收），不支持微信

---

## 一、业务背景

V1.0全部免费，DeepSeek API按Token计费成本高。引入分层付费体系。

---

## 二、四级会员体系

### 2.1 等级定义

| 等级 | 名称 | 每日次数 | 上下文 | 档案同步 | 深度分析 | 导出 | 家庭共享 |
|------|------|---------|--------|---------|---------|------|---------|
| L0 | 普通用户 | 3次 | 5轮 | 手动 | ✗ | ✗ | ✗ |
| L1 | 白银会员 | 20次 | 15轮 | 自动 | ✗ | ✓ | 1个共享位 |
| L2 | 黄金会员 | 50次 | 30轮 | 自动 | ✗ | ✓ | 3个共享位 |
| L3 | 铂金会员 | 无限 | 50轮 | 自动+深度 | ✓ | ✓ | 无限共享 |

### 2.2 家庭共享（预留，V2.1实现）

- L1可共享给1人，L2共享给3人，L3无限
- 共享成员使用主账号的每日配额池
- V2.0仅完成数据模型预留，不实现前端

---

## 三、订阅方案与定价

| 方案 | 等级 | 周期 | 正常价 | 首月体验价 |
|------|------|------|--------|-----------|
| 白银月卡 | L1 | 1个月 | ¥19.9 | ¥0.1 |
| 白银季卡 | L1 | 3个月 | ¥49.9 | — |
| 黄金月卡 | L2 | 1个月 | ¥39.9 | ¥9.9 |
| 黄金季卡 | L2 | 3个月 | ¥99.9 | — |
| 铂金月卡 | L3 | 1个月 | ¥69.9 | ¥19.9 |
| 铂金年卡 | L3 | 12个月 | ¥599 | — |

**首月体验价规则：**
- 每个用户仅可享受一次首月体验价
- 续费按正常价执行

---

## 四、订阅与退款规则

- 同一用户只持有一个有效等级，新订阅叠加时长（非覆盖）
- 到期前3天App推送续费提醒
- 到期后24小时宽限期，保留标识但暂停权益
- 宽限期过后自动降为L0

### 4.1 退款规则

- 用户可在App内申请退款
- 需经过管理员审核（R002/R003权限）
- 审核通过后按剩余天数比例退款：`退款金额 = 支付金额 × (剩余天数 / 总天数)`
- 退款后立即降为L0，已使用天数不退还
- 首月体验价订单不支持退款

---

## 五、超级管理员直接开通会员

### 5.1 功能说明

超级管理员（R002）可在后台直接为用户开通/延长/调整会员，无需走支付流程。适用于：
- 内部员工福利
- 合作渠道赠品
- 投诉补偿
- 线下收款后手动开通

### 5.2 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/admin/member/grant` | 手动开通/延长会员
| POST | `/api/v1/admin/member/revoke` | 手动撤销会员资格

### 5.3 入参

```json
{
  "userId": 10001,
  "levelCode": "L2",
  "durationDays": 30,
  "reason": "内部员工福利"
}
```

---

## 六、邀请奖励机制（预留，V2.1实现）

- 每个用户拥有唯一邀请码和邀请二维码
- 被邀请人通过邀请码注册并成为付费会员后，邀请人获得奖励
- 奖励内容：按被邀请人首笔订单金额的20%折算为会员天数
- V2.0仅完成 `users` 表 `invite_code`、`invited_by` 字段预留

---

## 七、数据库设计

### 7.1 新增表（5张）

| 表名 | 说明 |
|------|------|
| `member_levels` | 会员等级定义 |
| `user_memberships` | 用户会员记录 |
| `subscription_plans` | 订阅方案 |
| `payment_orders` | 支付订单 |
| `refund_requests` | 退款申请 |

### 7.2 users表新增字段

```sql
ALTER TABLE users ADD COLUMN member_level VARCHAR(8) DEFAULT 'L0' COMMENT '当前会员等级';
ALTER TABLE users ADD COLUMN member_expire_time DATETIME DEFAULT NULL COMMENT '会员到期时间';
ALTER TABLE users ADD COLUMN invite_code VARCHAR(16) DEFAULT NULL COMMENT '邀请码';
ALTER TABLE users ADD COLUMN invited_by BIGINT UNSIGNED DEFAULT NULL COMMENT '邀请人ID';
ALTER TABLE users ADD COLUMN first_purchase TINYINT DEFAULT 0 COMMENT '是否首购(用于首月体验价)';
```

---
## 八、API接口设计

### 8.1 会员（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/member/status` | 当前用户会员状态 |
| GET | `/api/v1/member/levels` | 所有会员等级权益说明 |
| GET | `/api/v1/member/history` | 订阅历史 |

### 8.2 支付（需登录，仅支付宝）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/payment/plans` | 所有上架订阅方案（含首月价） |
| POST | `/api/v1/payment/order` | 创建支付订单（planId） |
| GET | `/api/v1/payment/order/{orderNo}` | 查询订单状态 |
| POST | `/api/v1/payment/callback/alipay` | 支付宝异步回调（无需登录） |
| POST | `/api/v1/payment/order/{orderNo}/cancel` | 取消待支付订单 |
| POST | `/api/v1/payment/refund/apply` | 申请退款 |

### 8.3 管理后台（R002/R003）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/admin/member/users` | 会员用户列表 |
| GET | `/api/v1/admin/member/stats` | 会员统计 |
| POST | `/api/v1/admin/member/grant` | 手动开通会员（R002） |
| POST | `/api/v1/admin/member/revoke` | 撤销会员（R002） |
| GET | `/api/v1/admin/payment/orders` | 支付订单列表 |
| GET | `/api/v1/admin/payment/refunds` | 退款申请列表 |
| POST | `/api/v1/admin/payment/refund/{id}/approve` | 审核通过退款 |
| POST | `/api/v1/admin/payment/refund/{id}/reject` | 驳回退款 |

---

## 九、SecurityConfig白名单新增

```java
"/api/v1/payment/callback/**"  // 支付宝回调无需认证
```

---

## 十、企业/机构会员（预留，V2.1）

- 预留 `member_levels` 增加 B1/B2/B3 企业等级
- 企业管理员可批量管理员工子账号
- 企业按年签约，定制报价
- V2.0仅预留字段设计，不实现

---
## 十一、错误码

| 错误码 | 说明 |
|--------|------|
| 4005 | 每日咨询配额已用完 |
| 5001 | 会员等级不存在 |
| 5002 | 方案不存在或已下架 |
| 5003 | 订单不存在 |
| 5004 | 订单已过期 |
| 5005 | 订单已支付 |
| 5006 | 金额不匹配 |
| 5007 | 支付宝验签失败 |
| 5008 | 已有生效会员 |
| 5009 | 首月体验价已使用 |
| 5010 | 退款金额计算异常 |
| 5011 | 体验价订单不支持退款 |
| 5012 | 退款审核已处理 |

---

## 十二、开发排期

| 阶段 | 时间 | 内容 |
|------|------|------|
| DDL+种子数据 | 0.5天 | 5张表+users扩展+初始数据 |
| 会员模块 | 1天 | Controller+Service+Mapper |
| 支付模块 | 1.5天 | 订单+支付宝回调 |
| 退款流程 | 0.5天 | 申请+审核 |
| 管理后台接口 | 0.5天 | grant/revoke/stats |
| 咨询配额改造 | 0.5天 | Redis配额校验 |
| 联调测试 | 0.5天 | 支付宝沙箱+压测 |
| **合计** | **5天** | |
