/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Schema         : neusoft_health

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 20/05/2026 10:00:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 数据库表外键关联语句
-- 说明：根据业务逻辑建立表之间的参照关系
-- 执行方式：mysql -h localhost -P 3306 -u root -p neusoft_health < neusoft_health_foreign_keys.sql
-- ============================================================

-- ----------------------------
-- 用户相关外键
-- ----------------------------

-- 用户表自引用：邀请人关系
ALTER TABLE `users`
    ADD CONSTRAINT `fk_users_invited_by` FOREIGN KEY (`invited_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- 用户角色关联外键
-- ----------------------------

ALTER TABLE `user_roles`
    ADD CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `user_roles`
    ADD CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 角色权限关联外键
-- ----------------------------

ALTER TABLE `role_permissions`
    ADD CONSTRAINT `fk_role_permissions_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `role_permissions`
    ADD CONSTRAINT `fk_role_permissions_perm_id` FOREIGN KEY (`perm_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 咨询会话外键
-- ----------------------------

ALTER TABLE `sessions`
    ADD CONSTRAINT `fk_sessions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 咨询消息外键
-- ----------------------------

ALTER TABLE `messages`
    ADD CONSTRAINT `fk_messages_session_id` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `messages`
    ADD CONSTRAINT `fk_messages_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `messages`
    ADD CONSTRAINT `fk_messages_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- 用户收藏外键
-- ----------------------------

ALTER TABLE `user_favorites`
    ADD CONSTRAINT `fk_user_favorites_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `user_favorites`
    ADD CONSTRAINT `fk_user_favorites_message_id` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 用户健康档案外键
-- ----------------------------

ALTER TABLE `user_health_profiles`
    ADD CONSTRAINT `fk_user_health_profiles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 用户设置外键
-- ----------------------------

ALTER TABLE `user_settings`
    ADD CONSTRAINT `fk_user_settings_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- 会员模块外键
-- ----------------------------

-- 用户会员记录外键
ALTER TABLE `user_memberships`
    ADD CONSTRAINT `fk_user_memberships_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `user_memberships`
    ADD CONSTRAINT `fk_user_memberships_level_code` FOREIGN KEY (`level_code`) REFERENCES `member_levels` (`level_code`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `user_memberships`
    ADD CONSTRAINT `fk_user_memberships_order_id` FOREIGN KEY (`source_order_id`) REFERENCES `payment_orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- 订阅方案外键
ALTER TABLE `subscription_plans`
    ADD CONSTRAINT `fk_subscription_plans_level_code` FOREIGN KEY (`level_code`) REFERENCES `member_levels` (`level_code`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- 支付订单外键
ALTER TABLE `payment_orders`
    ADD CONSTRAINT `fk_payment_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `payment_orders`
    ADD CONSTRAINT `fk_payment_orders_plan_id` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plans` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- 退款申请外键
ALTER TABLE `refund_requests`
    ADD CONSTRAINT `fk_refund_requests_order_id` FOREIGN KEY (`order_id`) REFERENCES `payment_orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `refund_requests`
    ADD CONSTRAINT `fk_refund_requests_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `refund_requests`
    ADD CONSTRAINT `fk_refund_requests_handled_by` FOREIGN KEY (`handled_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- 日志表外键
-- ----------------------------

-- 紧急情况日志外键
ALTER TABLE `emergency_logs`
    ADD CONSTRAINT `fk_emergency_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `emergency_logs`
    ADD CONSTRAINT `fk_emergency_logs_message_id` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- 操作日志外键
ALTER TABLE `operation_logs`
    ADD CONSTRAINT `fk_operation_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- 用户登录日志外键
ALTER TABLE `user_login_logs`
    ADD CONSTRAINT `fk_user_login_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- ----------------------------
-- FAQ外键
-- ----------------------------

ALTER TABLE `faqs`
    ADD CONSTRAINT `fk_faqs_category_id` FOREIGN KEY (`category_id`) REFERENCES `faq_categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 外键关联关系总结
-- ============================================================

/*
表关联关系一览：

1. 用户中心
├── users (用户表)
│   ├── invited_by → users.id (自引用)
│   └── user_roles.user_id → users.id
│   └── user_settings.user_id → users.id
│   └── user_health_profiles.user_id → users.id

2. 权限系统
├── roles (角色表)
│   └── user_roles.role_id → roles.id
│   └── role_permissions.role_id → roles.id
└── permissions (权限表)
    └── role_permissions.perm_id → permissions.id

3. 咨询模块
├── sessions (会话表)
│   ├── user_id → users.id
│   └── messages.session_id → sessions.id
└── messages (消息表)
    ├── user_id → users.id
    ├── reviewed_by → users.id
    └── user_favorites.message_id → messages.id

4. 会员模块
├── member_levels (会员等级)
│   ├── user_memberships.level_code → member_levels.level_code
│   └── subscription_plans.level_code → member_levels.level_code
├── subscription_plans (订阅方案)
│   └── payment_orders.plan_id → subscription_plans.id
├── payment_orders (支付订单)
│   ├── user_id → users.id
│   └── user_memberships.source_order_id → payment_orders.id
│   └── refund_requests.order_id → payment_orders.id
├── user_memberships (会员记录)
│   └── user_id → users.id
└── refund_requests (退款申请)
    ├── user_id → users.id
    └── handled_by → users.id

5. 日志系统
├── emergency_logs (紧急日志)
│   ├── user_id → users.id
│   └── message_id → messages.id
├── operation_logs (操作日志)
│   └── user_id → users.id
└── user_login_logs (登录日志)
    └── user_id → users.id

6. FAQ系统
├── faq_categories (分类)
│   └── faqs.category_id → faq_categories.id
└── faqs (常见问题)
*/