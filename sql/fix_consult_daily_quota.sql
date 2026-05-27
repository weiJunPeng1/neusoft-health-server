-- ============================================================
-- 修复脚本: 创建 consult_daily_quota 表
-- 原因: 后端报错 Table 'neusoft_health.consult_daily_quota' doesn't exist
-- 对应实体: ConsultDailyQuota.java
-- 对应Mapper: ConsultDailyQuotaMapper.java
-- 执行方式: 在 MySQL 客户端中执行此脚本
-- ============================================================

USE neusoft_health;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for consult_daily_quota
-- ----------------------------
DROP TABLE IF EXISTS `consult_daily_quota`;
CREATE TABLE `consult_daily_quota`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `consult_date` date NOT NULL COMMENT '咨询日期',
  `consult_count` int NOT NULL DEFAULT 0 COMMENT '当日咨询次数',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `consult_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_consult_date`(`consult_date` ASC) USING BTREE,
  CONSTRAINT `fk_consult_daily_quota_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户每日咨询配额使用记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consult_daily_quota
-- ----------------------------
-- 用户3 (L0普通用户，每日配额3次) 的历史使用记录示例
INSERT INTO `consult_daily_quota` VALUES (1, 3, '2026-05-24', 2, '2026-05-24 09:15:00', '2026-05-24 14:30:00');
INSERT INTO `consult_daily_quota` VALUES (2, 3, '2026-05-25', 3, '2026-05-25 08:20:00', '2026-05-25 16:45:00');
INSERT INTO `consult_daily_quota` VALUES (3, 3, '2026-05-26', 1, '2026-05-26 10:00:00', '2026-05-26 10:00:00');
-- 用户2 (L3铂金会员，配额无限) 的使用记录示例
INSERT INTO `consult_daily_quota` VALUES (4, 2, '2026-05-25', 8, '2026-05-25 09:00:00', '2026-05-25 21:30:00');
INSERT INTO `consult_daily_quota` VALUES (5, 2, '2026-05-26', 3, '2026-05-26 08:30:00', '2026-05-26 11:00:00');

SET FOREIGN_KEY_CHECKS = 1;
