# 东软智慧健康咨询系统 V2.1 — 前端预览 + 导航重构 PRD

**文档版本**：V2.1
**编写日期**：2026-05-20
**依赖文档**：PRD.md (V1.0)、PRD-V2-member-payment.md (V2.0)、uniapp-design-doc.md (V1.2)

---

## 一、版本目标

V2.1 在 V2.0 会员支付体系的基础上，完成以下工作：
1. 新增 5 个前端预览页面，HTML 预览总数达到 20 页
2. 重构全局导航结构，统一一级Tab与子页面层级
3. 增强页面间关联链路，确保用户从任意页面可触达核心服务
4. 同步更新 UniApp 设计文档与 PRD

---

## 二、新增页面规格

### 2.1 支付结算页 (payment.html)

| 属性 | 说明 |
|------|------|
| 入口 | 会员中心 → 订阅方案 → 创建订单 → 支付页 |
| 核心功能 | 支付方式选择（支付宝/微信）、15分钟倒计时、Canvas二维码、支付成功弹窗 |
| 订单信息展示 | 商品名称、订单编号、创建时间、有效期限、应付金额 |
| 支付方式 | 支付宝（默认，带模拟二维码）、微信支付（显示开发中） |
| 安全提示 | 数据加密、安全支付、资金保障三项安全标识 |
| 演示功能 | "模拟支付成功"按钮，点击后展示成功弹窗和会员权益摘要 |
| 倒计时 | 15分钟（900秒），最后3分钟数字变红，超时跳转订单页 |

### 2.2 AI报告解读页 (health-report.html)

| 属性 | 说明 |
|------|------|
| 入口 | 首页/服务导航 → "报告解读" / AI健康助手 → 快捷功能 |
| 核心功能 | 上传体检报告（PDF/JPG/PNG）、AI指标分析、健康改善建议 |
| 上传交互 | 拖拽上传区域 + 点击上传 + 模拟上传演示按钮 |
| AI分析过程 | 加载动画（跳动的Loading点 + "AI正在分析您的报告..."提示） |
| 报告概览 | 姓名、检查日期、报告类型、综合评估（良好/需关注） |
| 指标分析 | 5项示例指标（血压/血糖/胆固醇/尿酸/心率），每项带进度条 + 正常/偏高/偏低标签 |
| AI建议 | 异常指标警告卡片（橙色）+ 健康改善建议卡片（绿色），含饮食/运动/复查/就医建议 |
| 操作按钮 | 分享报告、保存到健康档案 |

### 2.3 药品查询页 (medicine-search.html)

| 属性 | 说明 |
|------|------|
| 入口 | 首页/服务导航 → "药品查询" |
| 核心功能 | 关键词搜索 + 分类筛选 + 药品卡片列表 + 详情弹窗 |
| 分类筛选 | 全部、感冒用药、消炎镇痛、胃肠消化、心血管、皮肤外用、维生素/保健品（7类） |
| 药品卡片 | 图标、名称、分类标签（OTC/RX处方药）、规格、厂家、用途、参考价 |
| 详情弹窗 | 适应症、用法用量、不良反应、注意事项、免责声明 |
| 示例药品 | 布洛芬、对乙酰氨基酚、阿莫西林、奥美拉唑、阿托伐他汀、莫匹罗星、维生素C |
| 搜索过滤 | 实时过滤，匹配药品名称和描述文本 |

### 2.4 常见问题页 (faq.html)

| 属性 | 说明 |
|------|------|
| 入口 | 首页/服务导航 → "常见问题" |
| 核心功能 | FAQ分类Tab + 手风琴展开/收起 + 热门标记 |
| 分类Tab | 全部、账户相关、会员订阅、咨询问诊、AI服务、支付退款、隐私安全（7类） |
| FAQ条目 | 14条常见问题，覆盖注册、密码、会员、咨询、AI、支付、隐私 |
| 交互效果 | CSS transition 手风琴展开/收起，箭头旋转动画 |
| 底部关联 | 相关服务推荐按钮：AI健康咨询、药品查询、消息通知 |

### 2.5 消息通知页 (notification.html)

| 属性 | 说明 |
|------|------|
| 入口 | 首页/服务导航/我的 → "消息通知" |
| 核心功能 | 通知分类Tab + 未读标记 + 详情弹窗 + 全部已读 |
| 分类Tab | 全部、系统（带未读数角标3）、会员、咨询 |
| 通知列表 | 10条示例通知，含系统公告、隐私政策、会员续费、咨询回复、紧急识别等 |
| 未读标记 | 蓝色背景高亮 + 右侧 `::before` 红点 |
| 详情弹窗 | 标题 + 时间 + 正文，点击遮罩或关闭按钮关闭 |
| 布局说明 | 导航栏 fixed，内容区 `padding-top: 62px`，Tab栏 `position: sticky; top: 0` |

### 2.6 服务导航页 (services.html)

| 属性 | 说明 |
|------|------|
| 位置 | 一级Tab（首页/服务/会员/我的） |
| 核心功能 | 全服务分类导航，4个分类区 + 快捷入口 |
| 分类布局 | 2x2 网格卡片，每张含图标、标题、描述、标签 |
| AI核心服务区 | AI健康咨询(HOT)、健康助手(HOT)、AI报告解读(NEW)、健康搜索 |
| 会员与支付区 | 会员中心(VIP)、我的订单、支付宝支付(NEW) |
| 健康工具区 | 健康档案、药品查询(NEW)、常见问题、消息通知 |
| 快捷入口 | 咨询历史、我的收藏、健康资讯（横向3列） |
| 底部导航 | 首页 / 服务(active) / 会员 / 我的 |

---

## 三、导航结构重构

### 3.1 变更摘要

| 变更项 | V2.0（旧） | V2.1（新） |
|--------|-----------|-----------|
| 一级Tab | 首页/问诊/会员/我的 | 首页/服务/会员/我的 |
| consult.html | 一级Tab页，底部有Tab栏 | 子页面，左上角返回按钮，无底部Tab |
| health-record.html | 子页面，但底部有Tab栏 | 子页面，返回按钮，无底部Tab |
| messages.html | 子页面，但底部有Tab栏 | 子页面，返回按钮，无底部Tab |
| services.html | 不存在 | 新增一级Tab页 |

### 3.2 导航层级图

```
一级Tab（4个，有底部导航栏）
├── 首页 (index.html)
│   ├── 更多服务区域 → 药品查询/报告解读/FAQ/通知
│   ├── 精选专题 → 健康资讯
│   ├── AI健康助手入口 → health-assistant.html
│   ├── 健康数据卡片 → health-record.html
│   └── 搜索栏 → search.html
│
├── 服务 (services.html)
│   ├── AI核心服务
│   │   ├── AI健康咨询 → consult.html
│   │   ├── 健康助手 → health-assistant.html
│   │   ├── AI报告解读 → health-report.html
│   │   └── 健康搜索 → search.html
│   ├── 会员与支付
│   │   ├── 会员中心 → member.html
│   │   ├── 我的订单 → order.html
│   │   └── 支付宝支付 → payment.html
│   ├── 健康工具
│   │   ├── 健康档案 → health-record.html
│   │   ├── 药品查询 → medicine-search.html
│   │   ├── 常见问题 → faq.html
│   │   └── 消息通知 → notification.html
│   └── 快捷入口
│       ├── 咨询历史 → consult-history.html
│       ├── 我的收藏 → collections.html
│       └── 健康资讯 → news.html
│
├── 会员 (member.html)
│   ├── 订阅方案 → 创建订单 → order.html
│   └── 支付 → payment.html
│
└── 我的 (profile.html)
    ├── 设置 → settings.html
    ├── 健康档案 → health-record.html
    ├── 咨询历史 → consult-history.html
    ├── 收藏 → collections.html
    ├── 消息中心 → messages.html → notification.html
    └── 订单 → order.html
```

### 3.3 子页面通用规则

- 所有子页面使用 `nav-header` + `nav-back`（返回按钮）
- `nav-back` 默认调用 `window.history.back()`，无历史时回退到 `index.html`
- 子页面不使用底部 `tab-bar`
- 页面内容区域使用 `padding-top` 推开固定导航栏

---

## 四、CSS 增强

### 4.1 新增动画类

| 动画类 | 效果 | 用途 |
|--------|------|------|
| `pageEnter` | opacity + translateY | 页面入场过渡 |
| `messageSlideIn` | translateY + opacity | 消息气泡滑入 |
| `modalPopIn` | scale + translateY | 弹窗弹出 |
| `toastSlideUp` | translate | Toast提示滑入 |
| `bounceIn` | scale缩放弹跳 | 支付二维码弹入 |
| `heartbeat` | scale脉冲 | 紧急医疗图标 |
| `shimmer` | 渐变色移动 | 骨架屏加载 |
| `floatUp` | translateY渐显 | 列表项逐条出现 |
| `slideInRight` | translateX滑入 | 侧滑入场 |
| `fadeIn` | opacity渐显 | 淡入效果 |
| `scaleIn` | scale放大渐显 | 缩放入场 |
| `pulse` | scale脉冲 | 持续闪烁 |

### 4.2 交互增强

- 卡片按下反馈：`:active` 时 `scale(0.98)`
- 按钮按下反馈：`:active` 时 `scale(0.96)` + 光晕扩散
- 快捷问题按钮 hover 变色
- 搜索框 focus 光晕
- 滚动条美化（4px 圆角）
- 全局 `focus-visible` 可访问性轮廓

---

## 五、UniApp 开发预备

以下为从 HTML 预览到 UniApp 开发的关键映射：

| HTML文件 | UniApp页面路由 | 组件复用 |
|----------|---------------|----------|
| index.html | pages/index/index | ChatMessage, ServiceCard |
| services.html | pages/services/index | ServiceCard (2x2 grid) |
| consult.html | pages/consult/index | ChatMessage, QuickQuestions |
| member.html | pages/member/index | MemberCard, PlanCard |
| profile.html | pages/profile/index | ProfileHeader, StatsRow |
| payment.html | pages/payment/index | CountdownRing, QRCode |
| health-report.html | pages/health-report/index | UploadZone, IndicatorRow, AdviceCard |
| medicine-search.html | pages/medicine-search/index | SearchBar, CategoryPill, MedCard, MedDetail |
| faq.html | pages/faq/index | FaqCategory, FaqItem (accordion) |
| notification.html | pages/notification/index | NotifTab, NotifItem, NotifDetail |

### 5.1 公共组件提取

以下组件可从 HTML 提取为 UniApp 公共组件：
- `NavHeader` — 导航栏（含返回按钮/标题/右侧操作）
- `TabBar` — 底部一级导航（首页/服务/会员/我的）
- `Card` — 通用卡片容器
- `Modal` — 通用弹窗
- `Toast` — 轻提示
- `Skeleton` — 骨架屏

### 5.2 状态管理预备

- `useUserStore` — 用户信息、登录状态、会员等级
- `useMemberStore` — 会员状态、订阅方案、每日配额
- `useConsultStore` — 咨询会话、消息列表
- `useNotificationStore` — 通知列表、未读数

---

## 六、验收标准

1. **导航完整性**：从任意页面可通过底部Tab或返回按钮回到首页
2. **页面间关联**：服务导航页可触达全部20个页面
3. **子页面清理**：consult/health-record/messages 不再显示底部Tab
4. **设计文档同步**：uniapp-design-doc.md 版本 V1.2 反映所有页面和导航变更
5. **CSS一致性**：所有页面使用统一的 styles.css 和动画系统

---

**文档结束**


---

## 十二、HTML预览与UniApp同步策略（V2.1新增）

### 12.1 设计原则

- **HTML预览 ≡ UniApp页面**：所有HTML预览文件与UniApp的`.vue`页面保持完全一致
- **HTML驱动开发**：先完成HTML预览验证交互，再迁移到UniApp
- **全局CSS共享**：`styles.css`嵌入`App.vue`作为全局样式
- **统一导航结构**：4个一级Tab + 16个子页面，所有子页面有返回按钮

### 12.2 页面清单（20页）

| # | HTML文件 | UniApp页面 | 类型 | 导航 |
|---|---------|-----------|------|------|
| 1 | index.html | pages/index/index.vue | Tab | 首页 |
| 2 | services.html | pages/services/index.vue | Tab | 服务 |
| 3 | member.html | pages/member/index.vue | Tab | 会员 |
| 4 | profile.html | pages/profile/index.vue | Tab | 我的 |
| 5 | consult.html | pages/consult/index.vue | 子页 | ←返回 |
| 6 | health-assistant.html | pages/health-assistant/index.vue | 子页 | ←返回 |
| 7 | health-report.html | pages/health-report/index.vue | 子页 | ←返回 |
| 8 | health-record.html | pages/health-record/index.vue | 子页 | ←返回 |
| 9 | medicine-search.html | pages/medicine-search/index.vue | 子页 | ←返回 |
| 10 | faq.html | pages/faq/index.vue | 子页 | ←返回 |
| 11 | notification.html | pages/notification/index.vue | 子页 | ←返回 |
| 12 | order.html | pages/order/index.vue | 子页 | ←返回 |
| 13 | payment.html | pages/payment/index.vue | 子页 | ←返回 |
| 14 | search.html | pages/search/index.vue | 子页 | ←返回 |
| 15 | login.html | pages/login/index.vue | 子页 | ←返回 |
| 16 | settings.html | pages/settings/index.vue | 子页 | ←返回 |
| 17 | consult-history.html | pages/consult-history/index.vue | 子页 | ←返回 |
| 18 | collections.html | pages/collections/index.vue | 子页 | ←返回 |
| 19 | messages.html | pages/messages/index.vue | 子页 | ←返回 |
| 20 | news.html | pages/news/index.vue | 子页 | ←返回 |

### 12.3 页面关联链路

- member.html → payment.html → order.html（会员→支付→订单）
- services.html → consult.html / health-report.html / faq.html / ...（服务→各子服务）
- index.html → 所有子页面（首页为调度中心）
- profile.html → settings / health-record / order / collections / ...（我的→个人相关）

### 12.4 接口对齐

前后端接口路径已完全对齐，详见 SKILL.md 第六章节。
前端API层文件：`api/auth.ts`、`api/user.ts`、`api/member.ts`、`api/payment.ts`、`api/consult.ts`
