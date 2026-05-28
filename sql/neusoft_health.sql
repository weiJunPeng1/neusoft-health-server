/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : localhost:3306
 Source Schema         : neusoft_health

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 28/05/2026 16:18:51
*/

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户每日咨询配额使用记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of consult_daily_quota
-- ----------------------------
INSERT INTO `consult_daily_quota` VALUES (1, 3, '2026-05-24', 2, '2026-05-24 09:15:00', '2026-05-24 14:30:00');
INSERT INTO `consult_daily_quota` VALUES (2, 3, '2026-05-25', 3, '2026-05-25 08:20:00', '2026-05-25 16:45:00');
INSERT INTO `consult_daily_quota` VALUES (3, 3, '2026-05-26', 1, '2026-05-26 10:00:00', '2026-05-26 10:00:00');
INSERT INTO `consult_daily_quota` VALUES (4, 2, '2026-05-25', 8, '2026-05-25 09:00:00', '2026-05-25 21:30:00');
INSERT INTO `consult_daily_quota` VALUES (5, 2, '2026-05-26', 3, '2026-05-26 08:30:00', '2026-05-26 11:00:00');

-- ----------------------------
-- Table structure for emergency_logs
-- ----------------------------
DROP TABLE IF EXISTS `emergency_logs`;
CREATE TABLE `emergency_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `message_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '消息ID',
  `keyword_matched` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '匹配到的紧急关键词',
  `handled` tinyint NULL DEFAULT 1 COMMENT '处理状态: 1=已处理',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `fk_emergency_logs_message_id`(`message_id` ASC) USING BTREE,
  CONSTRAINT `fk_emergency_logs_message_id` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_emergency_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '紧急情况日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of emergency_logs
-- ----------------------------

-- ----------------------------
-- Table structure for faq_categories
-- ----------------------------
DROP TABLE IF EXISTS `faq_categories`;
CREATE TABLE `faq_categories`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '图标URL',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status_sort`(`status` ASC, `sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'FAQ分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of faq_categories
-- ----------------------------
INSERT INTO `faq_categories` VALUES (1, '感冒发烧', '', 1, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `faq_categories` VALUES (2, '肠胃不适', '', 2, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `faq_categories` VALUES (3, '睡眠问题', '', 3, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `faq_categories` VALUES (4, '运动健康', '', 4, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `faq_categories` VALUES (5, '饮食营养', '', 5, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `faq_categories` VALUES (6, '皮肤问题', '', 6, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for faqs
-- ----------------------------
DROP TABLE IF EXISTS `faqs`;
CREATE TABLE `faqs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
  `question` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题内容',
  `preset_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预设答案',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_category_sort`(`category_id` ASC, `sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_faqs_category_id` FOREIGN KEY (`category_id`) REFERENCES `faq_categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '常见问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of faqs
-- ----------------------------
INSERT INTO `faqs` VALUES (1, 1, '感冒了应该吃什么药？', '普通感冒多休息多喝水，症状明显可服复方氨酚烷胺对症缓解', 1, 1, '2026-05-19 23:07:56', '2026-05-21 10:49:54', 0);
INSERT INTO `faqs` VALUES (2, 1, '发烧多少度需要去医院？', '体温超 38.5℃服药不退，或持续发烧超过 3 天建议就医', 2, 1, '2026-05-19 23:07:56', '2026-05-21 10:50:20', 0);
INSERT INTO `faqs` VALUES (3, 1, '感冒和流感有什么区别？', '流感症状更重，常伴高烧、全身酸痛，传染性更强', 3, 1, '2026-05-19 23:07:56', '2026-05-21 10:50:52', 0);
INSERT INTO `faqs` VALUES (4, 1, '感冒了可以运动吗？', '不建议剧烈运动，以休息为主，可适当慢走散步', 4, 1, '2026-05-19 23:07:56', '2026-05-21 10:51:19', 0);
INSERT INTO `faqs` VALUES (5, 1, '小孩感冒发烧怎么办？', '先物理降温，超 38.5℃遵医嘱用退烧药，精神差立即就医', 5, 1, '2026-05-19 23:07:56', '2026-05-21 10:52:06', 0);
INSERT INTO `faqs` VALUES (6, 1, '感冒了多喝水真的有用吗？', '有用，能补充水分、促进代谢和排汗降温', 6, 1, '2026-05-19 23:07:56', '2026-05-21 10:52:54', 0);
INSERT INTO `faqs` VALUES (7, 2, '胃疼怎么缓解？', '喝温热水，热敷腹部，暂时避免辛辣生冷食物', 1, 1, '2026-05-19 23:07:56', '2026-05-21 10:52:32', 0);
INSERT INTO `faqs` VALUES (8, 2, '拉肚子应该吃什么？', '吃清淡易消化的白粥、烂面条，避免油腻生冷', 2, 1, '2026-05-19 23:07:56', '2026-05-21 10:53:15', 0);
INSERT INTO `faqs` VALUES (9, 2, '经常胃胀气是什么原因？', '多与饮食过快、吃产气食物或消化不良有关多与饮食过快、吃产气食物或消化不良有关', 3, 1, '2026-05-19 23:07:56', '2026-05-21 10:54:56', 0);
INSERT INTO `faqs` VALUES (10, 2, '肠胃炎需要禁食吗？', '轻度无需禁食，吃清淡流食即可，严重呕吐可短暂禁食', 4, 1, '2026-05-19 23:07:56', '2026-05-21 10:55:31', 0);
INSERT INTO `faqs` VALUES (11, 2, '幽门螺杆菌感染怎么办？', '建议就医，遵医嘱进行四联疗法根除治疗', 5, 1, '2026-05-19 23:07:56', '2026-05-21 10:56:36', 0);
INSERT INTO `faqs` VALUES (12, 3, '失眠怎么快速入睡？', '睡前远离手机，保持卧室黑暗安静，可听舒缓白噪音', 1, 1, '2026-05-19 23:07:56', '2026-05-21 10:56:50', 0);
INSERT INTO `faqs` VALUES (13, 3, '每天睡几个小时最健康？', '成年人建议 7-9 小时，青少年 8-10 小时', 2, 1, '2026-05-19 23:07:56', '2026-05-21 10:56:54', 0);
INSERT INTO `faqs` VALUES (14, 3, '熬夜后怎么恢复？', '中午小憩 20-30 分钟，多吃蔬菜水果，避免连续熬夜', 3, 1, '2026-05-19 23:07:56', '2026-05-21 10:57:32', 0);
INSERT INTO `faqs` VALUES (15, 3, '打鼾是病吗？', '偶尔打鼾正常，若伴随呼吸暂停、白天嗜睡需就医', 4, 1, '2026-05-19 23:07:56', '2026-05-21 10:57:44', 0);
INSERT INTO `faqs` VALUES (16, 3, '褪黑素可以长期吃吗？', '不建议长期吃，可能产生依赖，影响自身激素分泌', 5, 1, '2026-05-19 23:07:56', '2026-05-21 10:57:58', 0);
INSERT INTO `faqs` VALUES (17, 4, '每天走多少步最健康？', '成年人每天 6000-8000 步即可，避免过量伤膝盖', 1, 1, '2026-05-19 23:07:56', '2026-05-21 10:58:11', 0);
INSERT INTO `faqs` VALUES (18, 4, '跑步伤膝盖怎么办？', '选合适跑鞋，跑前热身跑后拉伸，控制跑步强度时长', 2, 1, '2026-05-19 23:07:56', '2026-05-21 10:58:34', 0);
INSERT INTO `faqs` VALUES (19, 4, '运动后肌肉酸痛怎么缓解？', '拉伸放松，热敷按摩，保证充足休息', 3, 1, '2026-05-19 23:07:56', '2026-05-21 10:58:36', 0);
INSERT INTO `faqs` VALUES (20, 4, '什么时间运动效果最好？', '下午 3-5 点身体状态最佳，也可根据自身习惯选择', 4, 1, '2026-05-19 23:07:56', '2026-05-21 10:58:57', 0);
INSERT INTO `faqs` VALUES (21, 4, '久坐的人应该做什么运动？', '每小时起身活动 5 分钟，可做拉伸、深蹲、扩胸运动', 5, 1, '2026-05-19 23:07:56', '2026-05-21 10:59:06', 0);
INSERT INTO `faqs` VALUES (22, 5, '怎么吃才能减肥？', '控制总热量，多吃蛋白质和蔬菜，少吃高糖高油食物', 1, 1, '2026-05-19 23:07:56', '2026-05-21 10:59:16', 0);
INSERT INTO `faqs` VALUES (23, 5, '每天吃水果的最佳时间？', '两餐之间吃最好，避免空腹吃刺激性水果', 2, 1, '2026-05-19 23:07:56', '2026-05-21 10:59:23', 0);
INSERT INTO `faqs` VALUES (24, 5, '高血脂应该怎么饮食？', '低脂低盐，多吃粗粮、蔬菜和深海鱼，少吃动物内脏', 3, 1, '2026-05-19 23:07:56', '2026-05-21 10:59:32', 0);
INSERT INTO `faqs` VALUES (25, 5, '素食主义会营养不良吗？', '搭配合理不会，需注意补充蛋白质、铁和维生素 B12', 4, 1, '2026-05-19 23:07:56', '2026-05-21 10:59:39', 0);
INSERT INTO `faqs` VALUES (26, 5, '补钙应该吃什么？', '多喝牛奶酸奶，吃豆制品、深绿色蔬菜，多晒太阳', 5, 1, '2026-05-19 23:07:56', '2026-05-21 10:59:53', 0);
INSERT INTO `faqs` VALUES (27, 6, '长痘痘怎么消？', '清淡饮食规律作息，不要挤痘，严重可外用阿达帕林', 1, 1, '2026-05-19 23:07:56', '2026-05-21 11:00:00', 0);
INSERT INTO `faqs` VALUES (28, 6, '皮肤干燥起皮怎么办？', '多喝水，用保湿面霜，避免过度清洁和频繁去角质', 2, 1, '2026-05-19 23:07:56', '2026-05-21 11:00:07', 0);
INSERT INTO `faqs` VALUES (29, 6, '湿疹怎么治疗？', '避免接触过敏原，遵医嘱外用温和的糖皮质激素药膏', 3, 1, '2026-05-19 23:07:56', '2026-05-21 11:00:13', 0);
INSERT INTO `faqs` VALUES (30, 6, '防晒霜怎么选？', '日常选 SPF30+ PA+++，户外选 SPF50+ PA++++', 4, 1, '2026-05-19 23:07:56', '2026-05-21 11:00:23', 0);
INSERT INTO `faqs` VALUES (31, 6, '皮肤过敏了怎么办？', '立即远离过敏原，用清水洗脸，严重可口服氯雷他定', 5, 1, '2026-05-19 23:07:56', '2026-05-21 11:00:25', 0);

-- ----------------------------
-- Table structure for health_articles
-- ----------------------------
DROP TABLE IF EXISTS `health_articles`;
CREATE TABLE `health_articles`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `summary` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '文章摘要',
  `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '封面图URL',
  `content_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'H5详情页URL',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status_sort`(`status` ASC, `sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_articles
-- ----------------------------

-- ----------------------------
-- Table structure for health_search_cache
-- ----------------------------
DROP TABLE IF EXISTS `health_search_cache`;
CREATE TABLE `health_search_cache`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '缓存ID',
  `keyword` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '搜索关键词',
  `keyword_hash` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关键词MD5哈希(归一化后)，用于精确匹配',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'AI生成结果/人工编辑内容',
  `source` tinyint NULL DEFAULT 1 COMMENT '来源: 1=AI生成, 2=人工编辑',
  `hit_count` int NULL DEFAULT 1 COMMENT '命中次数',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0=否, 1=是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_keyword_hash`(`keyword_hash` ASC) USING BTREE,
  INDEX `idx_hit_count`(`hit_count` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康搜索AI缓存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_search_cache
-- ----------------------------
INSERT INTO `health_search_cache` VALUES (1, '中医养生', '4ddcb5763864652eb88d42a747b422e1', '中医养生强调“治未病”，通过调养身心预防疾病。以下是几个核心原则：\n\n1. **顺应四时**：根据春生、夏长、秋收、冬藏的自然规律调整作息与饮食，如夏季养心、冬季补肾。\n2. **平衡阴阳**：通过饮食（如寒热搭配）、运动（如太极拳）和情志调节，维持体内阴阳平衡。\n3. **调和脏腑**：重点养护脾胃（后天之本）和肾（先天之本），避免暴饮暴食、熬夜伤身。\n4. **疏通经络**：通过按摩、针灸或八段锦等促进气血运行，缓解疲劳。\n5. **情志养生**：保持心态平和，避免过喜、过怒等极端情绪伤及五脏。\n\n注意：个体体质差异大，建议咨询专业中医师辨证施养。若出现持续不适，请及时就医。', 1, 3, '2026-05-21 11:26:46', '2026-05-21 11:26:46', 0);
INSERT INTO `health_search_cache` VALUES (2, '阿达帕宁', '4f7c94cd4dc788d37ea6648b5a5da564', '阿达帕宁（Adapalene）是一种外用维A酸类药物，主要用于治疗痤疮（粉刺、痘痘）。以下是关键信息：\n\n1. **作用机制**：通过调节毛囊口角质细胞分化，减少粉刺形成，并具有抗炎作用。\n2. **使用方式**：每晚1次，清洁皮肤后取薄层涂抹于患处（避开眼、口、鼻周）。初期可能需建立耐受（如隔日使用）。\n3. **常见反应**：使用前2-4周可能出现皮肤干燥、脱屑、泛红或刺痛，属正常现象，通常随使用时间减轻。\n4. **注意事项**：避免与含过氧化苯甲酰、水杨酸等刺激性产品同时使用；白天需严格防晒（紫外线敏感）；孕妇、哺乳期女性慎用。\n5. **禁忌**：对维A酸类过敏者、皮肤破损或湿疹处禁用。\n\n如出现严重刺激或过敏（如红肿、水疱），请暂停使用并咨询皮肤科医生。本回复仅供参考，具体治疗请遵医嘱。', 1, 1, '2026-05-21 11:33:58', '2026-05-21 11:33:58', 0);
INSERT INTO `health_search_cache` VALUES (3, '制附子', '66487d7d929fb6e33e17fea40d2329a6', '制附子为毛茛科植物乌头的子根加工品，性大热，味辛、甘，有毒，归心、肾、脾经。主要功效与注意事项如下：\n\n1. **回阳救逆**：用于亡阳证，如冷汗自出、四肢厥冷、脉微欲绝，常与干姜、甘草配伍（如四逆汤）。\n\n2. **补火助阳**：适用于肾阳虚衰引起的畏寒肢冷、阳痿尿频，或脾肾阳虚导致的腹泻、水肿。\n\n3. **散寒止痛**：治疗寒湿痹痛、关节冷痛及心腹冷痛。\n\n4. **使用注意**：\n   - **必须炮制**：生附子毒性极强，需经炮制（如盐附子、黑顺片）并先煎30-60分钟以减毒。\n   - **禁忌人群**：孕妇、阴虚阳亢（如潮热盗汗）及热证患者忌用。\n   - **配伍禁忌**：不宜与半夏、瓜蒌、贝母、白及同用（“十八反”）。\n\n**重要提示**：本品为剧毒中药，需在中医师辨证指导下使用，切勿自行服用。若出现口舌麻木、恶心呕吐等中毒症状，应立即就医。', 1, 1, '2026-05-21 11:34:15', '2026-05-21 11:34:15', 0);
INSERT INTO `health_search_cache` VALUES (4, '头晕怎么办', '27c5c139bec81c723eb3d417386f3703', '头晕的应对方法需根据可能原因调整，以下为常见建议：\n\n1. **立即安全措施**：立即坐下或躺下，避免摔倒；保持环境安静，闭眼休息；避免突然转头或站立。\n\n2. **常见原因处理**：\n   - **低血糖**：若伴有心慌、出汗，可补充糖水、果汁或饼干。\n   - **体位性低血压**：缓慢起身，避免久蹲后猛站。\n   - **脱水/中暑**：移至阴凉处，补充淡盐水或电解质饮料。\n   - **前庭性眩晕**（如耳石症）：保持头部不动，避免快速转动。\n\n3. **需警惕的情况**：若伴随以下症状，请及时就医：\n   - 剧烈头痛、言语不清、肢体麻木（可能脑卒中）；\n   - 胸痛、心悸、呼吸困难（可能心脏问题）；\n   - 持续呕吐、视力模糊、意识障碍。\n\n注意：频繁发作或原因不明时，建议尽早就诊神经内科或耳鼻喉科。本回复仅供参考，不能替代专业诊疗。', 1, 4, '2026-05-21 11:34:28', '2026-05-21 11:34:28', 0);
INSERT INTO `health_search_cache` VALUES (5, '我头晕', '6a5ad9213befc4659968151cf6307ee9', '头晕可能由多种原因引起，以下为常见情况及建议：\n\n1. **体位性低血压**：突然站起时头晕、眼前发黑。建议动作放缓，避免久蹲后猛起。\n2. **低血糖**：伴心慌、出汗、乏力。可立即补充糖分（如糖果、果汁）。\n3. **贫血**：常伴面色苍白、疲劳。需均衡饮食，补充铁和维生素B12。\n4. **颈椎问题**：长期低头或姿势不良，可能压迫血管导致头晕。注意颈部活动，避免长时间固定姿势。\n5. **耳石症**：头部转动时短暂眩晕，可能伴恶心。建议就医进行复位治疗。\n6. **其他原因**：如感冒、高血压、药物副作用、过度疲劳等。\n\n**建议**：\n- 若头晕持续不缓解，或伴剧烈头痛、言语不清、肢体麻木、胸闷等症状，请立即就医。\n- 记录头晕发作的时间、诱因和伴随症状，便于医生诊断。\n\n（以上信息仅供参考，具体诊疗请咨询专业医生。）', 1, 1, '2026-05-21 11:35:27', '2026-05-21 11:35:27', 0);
INSERT INTO `health_search_cache` VALUES (6, '失眠', '513bbbefabc9ae9438b5fcdf666d173b', '失眠通常指难以入睡、维持睡眠或早醒，导致白天功能受影响的状态。常见原因及应对建议如下：\n\n**常见原因**\n1.  **心理因素**：压力、焦虑、抑郁等情绪问题最常见。\n2.  **生活习惯**：作息不规律、睡前使用电子产品、摄入咖啡因/酒精、缺乏运动。\n3.  **环境因素**：噪音、光线过强、温度不适。\n4.  **生理因素**：疼痛、疾病（如甲状腺问题）、药物副作用。\n5.  **睡眠障碍**：如睡眠呼吸暂停综合征、不宁腿综合征等。\n\n**应对建议**\n1.  **建立规律作息**：固定上床和起床时间，即使周末也尽量保持。\n2.  **优化睡眠环境**：保持卧室黑暗、安静、凉爽，床垫枕头舒适。\n3.  **放松身心**：睡前1小时避免剧烈活动，可尝试冥想、深呼吸、温水浴。\n4.  **调整饮食**：避免睡前进食过饱或饮用刺激性饮料，可少量摄入助眠食物（如温牛奶）。\n5.  **减少床与清醒活动的关联**：不在床上工作、看电视，培养“见床即困”的条件反射。\n\n若失眠持续超过一个月并严重影响生活，建议咨询医生以排除潜在疾病并获得针对性治疗（如认知行为疗法或药物）。\n\n*以上内容为一般性健康知识，不能替代专业医疗诊断。*', 1, 2, '2026-05-26 11:17:29', '2026-05-26 11:17:29', 0);
INSERT INTO `health_search_cache` VALUES (7, '我有些头晕', '7a786b2ff74889d4153fe8fa08d4ee79', '您好，头晕是一种常见但可能原因多样的症状。以下是一些常见原因和基础建议：\n\n**一、常见原因**\n1.  **生理性/环境因素**：如突然起身（体位性低血压）、睡眠不足、饥饿、脱水或处于闷热环境。\n2.  **内耳问题**：如良性阵发性位置性眩晕（耳石症），通常在头部特定位置时诱发短暂眩晕。\n3.  **血压波动**：高血压或低血压都可能引起头晕。\n4.  **贫血或低血糖**：可能导致大脑供氧或能量供应不足。\n5.  **其他**：焦虑、药物副作用、感冒或流感等也可能伴随头晕。\n\n**二、基础处理与建议**\n*   **立即行动**：发生时请坐下或躺下，防止跌倒。\n*   **检查与观察**：注意头晕发生的时间、频率、伴随症状（如恶心、耳鸣、视物旋转）。\n*   **基础尝试**：补充水分或食物，保证休息，缓慢改变体位。\n\n**重要提示**：如果头晕频繁发作、持续不缓解、症状严重，或伴有剧烈头痛、言语不清、肢体无力、胸痛、意识模糊等症状，**请务必及时就医**，由医生进行专业诊断。此信息仅供参考，不能替代专业医疗建议。', 1, 2, '2026-05-26 11:18:29', '2026-05-26 11:18:29', 0);

-- ----------------------------
-- Table structure for member_levels
-- ----------------------------
DROP TABLE IF EXISTS `member_levels`;
CREATE TABLE `member_levels`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level_code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级编码：L0/L1/L2/L3',
  `level_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级名称：普通用户/白银会员/黄金会员/铂金会员',
  `daily_quota` int NOT NULL DEFAULT 0 COMMENT '每日咨询配额（0表示无限）',
  `context_rounds` int NOT NULL DEFAULT 0 COMMENT '上下文轮数',
  `auto_sync` tinyint NULL DEFAULT 0 COMMENT '自动同步：0=否，1=是',
  `deep_analysis` tinyint NULL DEFAULT 0 COMMENT '深度分析：0=否，1=是',
  `export_enabled` tinyint NULL DEFAULT 0 COMMENT '导出功能：0=否，1=是',
  `share_limit` int NULL DEFAULT 0 COMMENT '共享人数限制（0表示无限）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重（升序）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0=否，1=是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level_code`(`level_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员等级定义' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_levels
-- ----------------------------
INSERT INTO `member_levels` VALUES (1, 'L0', '普通用户', 3, 5, 0, 0, 0, 0, 0, 1, '2026-05-20 09:30:13', '2026-05-20 09:30:13', 0);
INSERT INTO `member_levels` VALUES (2, 'L1', '白银会员', 20, 15, 1, 0, 1, 1, 1, 1, '2026-05-20 09:30:13', '2026-05-20 09:30:13', 0);
INSERT INTO `member_levels` VALUES (3, 'L2', '黄金会员', 50, 30, 1, 0, 1, 3, 2, 1, '2026-05-20 09:30:13', '2026-05-20 09:30:13', 0);
INSERT INTO `member_levels` VALUES (4, 'L3', '铂金会员', 0, 50, 1, 1, 1, 0, 3, 1, '2026-05-20 09:30:13', '2026-05-20 09:30:13', 0);

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint UNSIGNED NOT NULL COMMENT '会话ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色: user/assistant/system',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `content_type` tinyint NULL DEFAULT 1 COMMENT '内容类型: 1=文本',
  `is_emergency` tinyint NULL DEFAULT 0 COMMENT '是否触发紧急流程',
  `emergency_keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '触发的紧急关键词',
  `is_violation` tinyint NULL DEFAULT 0 COMMENT '是否违规',
  `violation_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '违规原因',
  `review_status` tinyint NULL DEFAULT 0 COMMENT '审核状态: 0=待审核, 1=通过, 2=违规',
  `reviewed_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `modified_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '人工修改后的内容',
  `api_call_duration` int NULL DEFAULT NULL COMMENT 'DeepSeek API调用耗时(ms)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC, `created_time` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `fk_messages_reviewed_by`(`reviewed_by` ASC) USING BTREE,
  CONSTRAINT `fk_messages_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_messages_session_id` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_messages_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '咨询消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 1, 2, 'user', '中午好', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:27:45', '2026-05-21 10:27:45', 0);
INSERT INTO `messages` VALUES (2, 1, 2, 'assistant', '中午好！请问有什么健康方面的问题需要咨询吗？我会尽力提供专业建议。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1117, '2026-05-21 10:27:46', '2026-05-21 10:27:46', 0);
INSERT INTO `messages` VALUES (3, 1, 2, 'user', '我最近有点不太舒服，不知道什么原因造成的', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:28:12', '2026-05-21 10:28:12', 0);
INSERT INTO `messages` VALUES (4, 1, 2, 'assistant', '了解您的不适。请问具体是哪里不舒服？比如是头痛、乏力、胃部不适，还是其他症状？持续多久了？有无发烧或疼痛？建议先多休息、多喝水，观察变化。若症状加重或持续，请及时就医。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1259, '2026-05-21 10:28:13', '2026-05-21 10:28:13', 0);
INSERT INTO `messages` VALUES (5, 1, 2, 'user', '头晕', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:28:23', '2026-05-21 10:28:23', 0);
INSERT INTO `messages` VALUES (6, 1, 2, 'assistant', '头晕可能由多种原因引起，如低血糖、低血压、颈椎问题或疲劳。建议您：\n\n1. **立即坐下或躺下**，避免摔倒。\n2. **补充水分**，喝点温糖水（若怀疑低血糖）。\n3. 监测血压，若持续头晕伴恶心、呕吐或视物模糊，请**及时就医**。\n\n注意：若您有高血压、糖尿病史，或头晕伴随肢体麻木、言语不清，需警惕脑血管问题，立即急诊。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1696, '2026-05-21 10:28:24', '2026-05-21 10:28:24', 0);
INSERT INTO `messages` VALUES (7, 2, 2, 'user', '如何改善睡眠', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:34:17', '2026-05-21 10:34:17', 0);
INSERT INTO `messages` VALUES (8, 2, 2, 'assistant', '改善睡眠可以从以下几点入手：\n1. **规律作息**：每天固定时间上床和起床，即使周末也尽量保持一致。\n2. **优化环境**：保持卧室安静、黑暗、凉爽，使用遮光窗帘或白噪音机。\n3. **减少刺激**：睡前1小时避免使用电子设备，不摄入咖啡因、尼古丁或大量食物。\n4. **放松身心**：尝试深呼吸、冥想或温水泡脚。\n5. **适度运动**：白天适当活动，但避免睡前剧烈运动。\n如果长期失眠或伴随其他症状，建议咨询医生。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1772, '2026-05-21 10:34:18', '2026-05-21 10:34:18', 0);
INSERT INTO `messages` VALUES (9, 1, 2, 'user', '我现在有点不舒服，头晕晕的，可能发烧了', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:41:29', '2026-05-21 10:41:29', 0);
INSERT INTO `messages` VALUES (10, 1, 2, 'assistant', '建议您立即测量体温。如果体温超过38.5℃或伴有寒战、头痛，可考虑服用退烧药（如对乙酰氨基酚），并多喝水、物理降温。若持续高烧不退，或出现呼吸困难、意识模糊，请**立即就医**。注意休息，避免劳累。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1429, '2026-05-21 10:41:31', '2026-05-21 10:41:31', 0);
INSERT INTO `messages` VALUES (11, 1, 2, 'user', '我现在呼吸困难', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:42:32', '2026-05-21 10:42:32', 0);
INSERT INTO `messages` VALUES (12, 1, 2, 'assistant', '**请立即拨打急救电话（120）或让家人送您去最近的急诊室！**\n\n呼吸困难可能由严重感染、心肺问题或过敏等引起，**切勿等待**。在等待救援时，请保持坐姿，解开衣领，尝试缓慢深呼吸。**不要自行驾车**。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1507, '2026-05-21 10:42:34', '2026-05-21 10:42:34', 0);
INSERT INTO `messages` VALUES (13, 3, 2, 'user', '头痛是什么原因', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 10:53:25', '2026-05-21 10:53:25', 0);
INSERT INTO `messages` VALUES (14, 3, 2, 'assistant', '头痛的原因很多，常见包括：紧张性头痛（压力、疲劳）、偏头痛（伴恶心、怕光）、感冒发热、睡眠不足、颈椎问题、血压异常等。少数情况与颅内病变（如炎症、肿瘤）有关。建议先休息、补充水分、避免强光。若头痛剧烈、持续不缓解、伴呕吐或肢体麻木，请及时就医。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1471, '2026-05-21 10:53:26', '2026-05-21 10:53:26', 0);
INSERT INTO `messages` VALUES (15, 3, 2, 'user', '滚滚滚', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-21 11:31:12', '2026-05-21 11:31:12', 0);
INSERT INTO `messages` VALUES (16, 3, 2, 'assistant', '很抱歉，我理解您可能感到烦躁或不适。作为健康助手，我的职责是提供专业建议。如果我的回答未解决您的问题，请告诉我具体症状（如头痛位置、持续时间、有无其他不适），我会尽力协助。若需紧急帮助，请立即就医。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1670, '2026-05-21 11:31:14', '2026-05-21 11:31:14', 0);
INSERT INTO `messages` VALUES (17, 4, 4, 'user', '如何改善睡眠', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-26 10:55:46', '2026-05-26 10:55:46', 0);
INSERT INTO `messages` VALUES (18, 4, 4, 'assistant', '抱歉，AI 服务暂时不可用，请稍后再试。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 427, '2026-05-26 10:55:46', '2026-05-26 10:55:46', 0);
INSERT INTO `messages` VALUES (19, 4, 4, 'user', '1 测试', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-26 10:59:11', '2026-05-26 10:59:11', 0);
INSERT INTO `messages` VALUES (20, 4, 4, 'assistant', '您好！系统运行正常，很高兴为您服务。😊\n\n我是东软健康咨询助手，可以为您提供专业的健康咨询建议，包括但不限于：\n\n- 常见疾病预防与护理\n- 饮食营养指导\n- 运动健身建议\n- 睡眠改善方法\n- 心理健康调适\n- 用药常识科普\n\n请问您有什么健康方面的问题需要咨询吗？', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 4290, '2026-05-26 10:59:15', '2026-05-26 10:59:15', 0);
INSERT INTO `messages` VALUES (21, 4, 4, 'user', '我可困', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-26 11:00:23', '2026-05-26 11:00:23', 0);
INSERT INTO `messages` VALUES (22, 4, 4, 'assistant', '您好！感到困倦可能有多种原因，给您几个建议：\n\n**如果是白天犯困：**\n- 短暂小憩15-20分钟，不要超过30分钟\n- 起身活动、伸展身体\n- 用冷水洗脸，喝杯温水\n- 到通风处呼吸新鲜空气\n\n**如果是长期疲倦，建议关注：**\n- **睡眠质量**：是否熬夜、睡眠不足（成人建议7-8小时）\n- **饮食**：避免高糖高油，注意补充蛋白质和维生素\n- **运动**：适度运动可改善精力\n- **健康问题**：贫血、甲状腺功能减退等也会导致疲乏\n\n如果长期感到疲惫且无法缓解，建议到医院做个检查，排除潜在健康问题。\n\n请问您是经常困还是今天特别累呢？', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 13336, '2026-05-26 11:00:36', '2026-05-26 11:00:36', 0);
INSERT INTO `messages` VALUES (23, 5, 4, 'user', '皮肤过敏处理', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-26 11:00:48', '2026-05-26 11:00:48', 0);
INSERT INTO `messages` VALUES (24, 5, 4, 'assistant', '皮肤过敏时，请按以下步骤处理：\n\n1.  **立即停止**接触可能的过敏原（如化妆品、金属、花粉等），并用清水轻柔清洁皮肤。\n2.  **避免搔抓**，以免加重皮损或引发感染。可冷敷患处以缓解红肿和瘙痒。\n3.  **使用温和的保湿霜**修复皮肤屏障。\n4.  若症状明显，可**遵医嘱**使用抗组胺药（如氯雷他定）或外用低效价激素药膏。\n5.  **密切观察**，若出现呼吸困难、全身皮疹或水疱等严重反应，**请立即就医**。\n\n记录可疑过敏原，有助于未来预防。复杂或反复发作的过敏，建议咨询皮肤科医生进行过敏原检测。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 10905, '2026-05-26 11:00:59', '2026-05-26 11:00:59', 0);
INSERT INTO `messages` VALUES (25, 6, 4, 'user', '感冒发烧怎么办', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-26 11:10:14', '2026-05-26 11:10:14', 0);
INSERT INTO `messages` VALUES (26, 6, 4, 'assistant', '感冒发烧时可采取以下措施：\n1. **充分休息**，避免劳累，保证睡眠。\n2. **补充水分**，多喝温水、温粥或汤类，防止脱水。\n3. **物理降温**：若体温低于38.5℃，可温水擦拭额头、腋下等部位。\n4. **药物缓解**：体温超过38.5℃可遵医嘱服用退烧药（如布洛芬、对乙酰氨基酚），或使用感冒药缓解症状。\n5. **饮食清淡**，选择易消化食物，避免油腻辛辣。\n\n**注意**：若发烧持续超过3天、体温超过39℃、出现呼吸急促、胸痛、意识模糊等症状，或婴幼儿、老年人、孕妇及有基础疾病者，请及时就医。切勿自行滥用抗生素。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 11872, '2026-05-26 11:10:26', '2026-05-26 11:10:26', 0);
INSERT INTO `messages` VALUES (27, 6, 4, 'user', '我胃疼应该吃什么药', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-26 11:12:42', '2026-05-26 11:12:42', 0);
INSERT INTO `messages` VALUES (28, 6, 4, 'assistant', '胃疼用药需谨慎，具体取决于病因。常见情况如下：\n\n1. **若为餐后饱胀、反酸**：可考虑**铝碳酸镁**、**硫糖铝**等胃黏膜保护剂，或**雷尼替丁**等抑酸药。\n2. **若为隐隐作痛、空腹痛**：可能与胃酸过多相关，可尝试**奥美拉唑**等质子泵抑制剂（短期使用）。\n3. **若伴随恶心、消化不良**：可尝试**多潘立酮**（吗丁啉）促进胃动力。\n\n**重要提示**：\n- 以上为常见非处方药参考，**强烈建议先就医或咨询药师**，明确病因后用药。\n- **避免**自行服用止痛药（如布洛芬），以免加重胃损伤。\n- 饮食宜清淡、规律，避免辛辣、生冷、油腻食物。\n\n**必须立即就医的情况**：\n- 剧烈疼痛、无法缓解\n- 呕血、黑便\n- 伴有发烧、体重明显下降\n- 疼痛向背部、胸部放射\n\n胃疼原因复杂，切勿长期自行用药，确诊是关键。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 14703, '2026-05-26 11:12:57', '2026-05-26 11:12:57', 0);
INSERT INTO `messages` VALUES (29, 7, 5, 'user', '头痛是什么原因', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-27 08:24:52', '2026-05-27 08:24:52', 0);
INSERT INTO `messages` VALUES (30, 7, 5, 'assistant', '头痛原因多样，常见包括：紧张性头痛（压力、疲劳）、偏头痛（常伴恶心、畏光）、感冒发热、高血压、颈椎问题或睡眠不足。偶尔头痛可休息、按摩太阳穴、多喝水缓解。若持续加重、伴呕吐、视力模糊或肢体麻木，需及时就医排查脑部问题。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1643, '2026-05-27 08:24:54', '2026-05-27 08:24:54', 0);
INSERT INTO `messages` VALUES (31, 7, 5, 'user', 'jhfgcjgkvjlk;blj;', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-27 08:25:12', '2026-05-27 08:25:12', 0);
INSERT INTO `messages` VALUES (32, 7, 5, 'assistant', '您好，您输入的内容似乎是乱码（无意义字符）。请重新描述您的健康问题或具体症状，例如头痛的部位、持续时间、伴随症状等，以便我为您提供有针对性的建议。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 1370, '2026-05-27 08:25:13', '2026-05-27 08:25:13', 0);
INSERT INTO `messages` VALUES (33, 8, 5, 'user', '头痛是什么原因', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-27 12:06:51', '2026-05-27 12:06:51', 0);
INSERT INTO `messages` VALUES (34, 8, 5, 'assistant', '抱歉，AI 服务暂时不可用，请稍后再试。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 905, '2026-05-27 12:06:52', '2026-05-27 12:06:52', 0);
INSERT INTO `messages` VALUES (35, 9, 2, 'user', '皮肤过敏处理', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-28 11:30:39', '2026-05-28 11:30:39', 0);
INSERT INTO `messages` VALUES (36, 9, 2, 'assistant', '皮肤过敏首先要立刻脱离可疑过敏原（如致敏食物、化妆品、花粉、刺激性洗护品等），避免抓挠、热水烫洗，暂停所有功效类护肤品。\n轻症仅局部泛红、瘙痒、起小皮疹时，可先用生理盐水冷敷舒缓，口服氯雷他定或西替利嗪等抗组胺药，多数可逐步缓解。\n若出现大面积红肿水疱、发热、呼吸困难、喉头肿胀等情况，请立刻就医。\n以上建议仅供参考，症状持续请及时就诊。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 14005, '2026-05-28 11:30:53', '2026-05-28 11:30:53', 0);
INSERT INTO `messages` VALUES (37, 10, 2, 'user', '血压高怎么办', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-28 12:30:25', '2026-05-28 12:30:25', 0);
INSERT INTO `messages` VALUES (38, 10, 2, 'assistant', '首先需明确是否为高血压：非同日3次测量上臂血压均≥140/90mmHg可确诊。\n如果仅是轻度升高、无不适，可先调整生活方式：每日盐摄入≤5g，少油少糖，戒烟限酒，规律作息，每周3-5次中等强度运动，控制体重，避免情绪激动，监测1-3个月看是否回落。\n若血压升高明显、有头晕头痛等不适，或生活调整后仍不达标，及时到心内科就诊，遵医嘱服用降压药，勿自行换药停药。\n\n本建议仅供参考，不能替代临床诊断，不适请及时就医。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 16589, '2026-05-28 12:30:42', '2026-05-28 12:30:42', 0);
INSERT INTO `messages` VALUES (39, 11, 2, 'user', '头痛是什么原因', 1, 0, '', 0, '', 0, NULL, NULL, NULL, NULL, '2026-05-28 13:33:30', '2026-05-28 13:33:30', 0);
INSERT INTO `messages` VALUES (40, 11, 2, 'assistant', '头痛常见原因分两类：\n1. 生理性：多和熬夜、劳累、压力大、情绪焦虑、受凉、饮酒或过量摄入咖啡浓茶有关，休息后大多可自行缓解。\n2. 病理性：原发性的常见偏头痛、紧张性头痛；继发性的可由高血压、鼻窦炎、颈椎病诱发，极少数和颅内感染、脑血管病变等严重问题相关。\n\n若头痛频繁发作、程度剧烈，伴恶心呕吐、视力模糊等不适，请及时就医。本建议仅供参考，不能替代专业医疗诊断。', 1, 0, '', 0, '', 1, NULL, NULL, NULL, 13149, '2026-05-28 13:33:43', '2026-05-28 13:33:43', 0);

-- ----------------------------
-- Table structure for operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作用户名',
  `operation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作模块',
  `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作对象类型',
  `target_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作对象ID',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方法',
  `request_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '响应结果(脱敏)',
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'IP地址',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'User-Agent',
  `duration` int NULL DEFAULT 0 COMMENT '执行耗时(ms)',
  `status` tinyint NULL DEFAULT 1 COMMENT '执行状态: 1=成功, 0=失败',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC, `created_time` ASC) USING BTREE,
  INDEX `idx_operation`(`operation` ASC, `created_time` ASC) USING BTREE,
  CONSTRAINT `fk_operation_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_logs
-- ----------------------------

-- ----------------------------
-- Table structure for payment_orders
-- ----------------------------
DROP TABLE IF EXISTS `payment_orders`;
CREATE TABLE `payment_orders`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号（MP+时间戳+随机串）',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `plan_id` bigint UNSIGNED NOT NULL COMMENT '订阅方案ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `pay_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'alipay' COMMENT '支付方式：alipay',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态：0 待支付 1 已支付 2 已取消 3 退款中 4 已到账 5 到账失败',
  `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方交易流水号',
  `paid_time` datetime NULL DEFAULT NULL COMMENT '支付完成时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '订单过期时间（创建后15分钟）',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0=否，1=是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE,
  INDEX `fk_payment_orders_plan_id`(`plan_id` ASC) USING BTREE,
  CONSTRAINT `fk_payment_orders_plan_id` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plans` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_payment_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_orders
-- ----------------------------
INSERT INTO `payment_orders` VALUES (1, 'MP20260525094401966200', 2, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-25 09:59:01', '2026-05-25 09:44:01', '2026-05-25 09:44:01', 0);
INSERT INTO `payment_orders` VALUES (2, 'MP20260525102044549100', 2, 1, 0.10, 'alipay', 1, 'SIM_1779675651005', '2026-05-25 10:20:51', '2026-05-25 10:35:45', '2026-05-25 10:20:45', '2026-05-25 10:20:45', 0);
INSERT INTO `payment_orders` VALUES (3, 'MP20260525102056563900', 2, 1, 19.90, 'alipay', 3, 'SIM_1779675661969', '2026-05-25 10:21:02', '2026-05-25 10:35:56', '2026-05-25 10:20:56', '2026-05-25 10:20:56', 0);
INSERT INTO `payment_orders` VALUES (4, 'MP20260525102138078600', 2, 3, 169.00, 'alipay', 2, NULL, NULL, '2026-05-25 10:36:38', '2026-05-25 10:21:38', '2026-05-25 10:21:38', 0);
INSERT INTO `payment_orders` VALUES (5, 'MP20260525103424143300', 2, 1, 19.90, 'alipay', 2, NULL, NULL, '2026-05-25 10:49:24', '2026-05-25 10:34:24', '2026-05-25 10:34:24', 0);
INSERT INTO `payment_orders` VALUES (6, 'MP20260525104039645500', 2, 1, 19.90, 'alipay', 2, NULL, NULL, '2026-05-25 10:55:40', '2026-05-25 10:40:40', '2026-05-25 10:40:40', 0);
INSERT INTO `payment_orders` VALUES (7, 'MP20260525104136708800', 2, 7, 69.90, 'alipay', 2, NULL, NULL, '2026-05-25 10:56:36', '2026-05-25 10:41:36', '2026-05-25 10:41:36', 0);
INSERT INTO `payment_orders` VALUES (8, 'MP20260525105525625400', 2, 7, 69.90, 'alipay', 3, 'SIM_1779678890165', '2026-05-25 11:14:50', '2026-05-25 11:10:26', '2026-05-25 10:55:26', '2026-05-25 10:55:26', 0);
INSERT INTO `payment_orders` VALUES (9, 'MP20260525112906080100', 2, 1, 19.90, 'alipay', 2, NULL, NULL, '2026-05-25 11:44:07', '2026-05-25 11:29:07', '2026-05-25 11:29:07', 0);
INSERT INTO `payment_orders` VALUES (10, 'MP20260525113351476900', 2, 1, 19.90, 'alipay', 2, NULL, NULL, '2026-05-25 11:48:52', '2026-05-25 11:33:52', '2026-05-25 11:33:52', 0);
INSERT INTO `payment_orders` VALUES (11, 'MP20260525113406290200', 2, 2, 49.90, 'alipay', 2, NULL, NULL, '2026-05-25 11:49:07', '2026-05-25 11:34:07', '2026-05-25 11:34:07', 0);
INSERT INTO `payment_orders` VALUES (12, 'MP20260525113421788400', 2, 4, 39.90, 'alipay', 2, NULL, NULL, '2026-05-25 11:49:22', '2026-05-25 11:34:22', '2026-05-25 11:34:22', 0);
INSERT INTO `payment_orders` VALUES (13, 'MP20260525113744842200', 2, 7, 69.90, 'alipay', 2, NULL, NULL, '2026-05-25 11:52:44', '2026-05-25 11:37:44', '2026-05-25 11:37:44', 0);
INSERT INTO `payment_orders` VALUES (14, 'MP20260525163206319000', 2, 1, 19.90, 'alipay', 2, NULL, NULL, '2026-05-25 16:47:06', '2026-05-25 16:32:06', '2026-05-25 16:32:06', 0);
INSERT INTO `payment_orders` VALUES (15, 'MP20260526110512059300', 4, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-26 11:20:13', '2026-05-26 11:05:13', '2026-05-26 11:05:13', 0);
INSERT INTO `payment_orders` VALUES (16, 'MP20260526110533344400', 4, 2, 49.90, 'alipay', 2, NULL, NULL, '2026-05-26 11:20:33', '2026-05-26 11:05:33', '2026-05-26 11:05:33', 0);
INSERT INTO `payment_orders` VALUES (17, 'MP20260526110940963500', 4, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-26 11:24:40', '2026-05-26 11:09:40', '2026-05-26 11:09:40', 0);
INSERT INTO `payment_orders` VALUES (18, 'MP20260526111927717600', 4, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-26 11:34:28', '2026-05-26 11:19:28', '2026-05-26 11:19:28', 0);
INSERT INTO `payment_orders` VALUES (19, 'MP20260526112241701100', 4, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-26 11:37:42', '2026-05-26 11:22:42', '2026-05-26 11:22:42', 0);
INSERT INTO `payment_orders` VALUES (20, 'MP20260526113107975800', 4, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-26 11:46:08', '2026-05-26 11:31:08', '2026-05-26 11:31:08', 0);
INSERT INTO `payment_orders` VALUES (21, 'MP20260526114500177900', 4, 9, 599.00, 'alipay', 1, 'SIM_1779767929331', '2026-05-26 11:58:49', '2026-05-26 12:00:00', '2026-05-26 11:45:00', '2026-05-26 11:45:00', 0);
INSERT INTO `payment_orders` VALUES (22, 'MP20260526114806828500', 4, 1, 0.10, 'alipay', 2, NULL, NULL, '2026-05-26 12:03:07', '2026-05-26 11:48:07', '2026-05-26 11:48:07', 0);
INSERT INTO `payment_orders` VALUES (23, 'MP20260526115858997900', 4, 9, 599.00, 'alipay', 2, NULL, NULL, '2026-05-26 12:13:59', '2026-05-26 11:58:59', '2026-05-26 11:58:59', 0);
INSERT INTO `payment_orders` VALUES (24, 'MP20260526120050878400', 4, 9, 599.00, 'alipay', 2, NULL, NULL, '2026-05-26 12:15:50', '2026-05-26 12:00:50', '2026-05-26 12:00:50', 0);
INSERT INTO `payment_orders` VALUES (25, 'MP20260526120612890600', 4, 9, 599.00, 'alipay', 1, '2026052622001462600509816525', '2026-05-26 12:06:36', '2026-05-26 12:21:13', '2026-05-26 12:06:13', '2026-05-26 12:06:13', 0);
INSERT INTO `payment_orders` VALUES (26, 'MP20260527084701995900', 5, 9, 599.00, 'alipay', 0, NULL, NULL, '2026-05-27 09:02:01', '2026-05-27 08:47:01', '2026-05-27 08:47:01', 0);
INSERT INTO `payment_orders` VALUES (27, 'MP20260527084802802600', 5, 1, 0.10, 'alipay', 0, NULL, NULL, '2026-05-27 09:03:02', '2026-05-27 08:48:02', '2026-05-27 08:48:02', 0);
INSERT INTO `payment_orders` VALUES (28, 'MP20260527103617718100', 2, 1, 19.90, 'alipay', 1, 'SIM_1779850153108', '2026-05-27 10:49:13', '2026-05-27 10:51:17', '2026-05-27 10:36:17', '2026-05-27 10:36:17', 0);
INSERT INTO `payment_orders` VALUES (29, 'MP20260528101353403600', 4, 1, 19.90, 'alipay', 0, NULL, NULL, '2026-05-28 10:28:54', '2026-05-28 10:13:54', '2026-05-28 10:13:54', 0);

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `perm_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `perm_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `perm_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'menu' COMMENT '权限类型: menu/button/data',
  `parent_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT '父权限ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_perm_code`(`perm_code` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES (1, 'user:dashboard', '系统首页', 'menu', 0, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (2, 'user:consultation', 'AI健康咨询', 'menu', 0, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (3, 'user:consultation:send', '发送咨询消息', 'button', 2, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (4, 'user:history', '咨询历史', 'menu', 0, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (5, 'user:history:delete', '删除咨询历史', 'button', 4, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (6, 'user:history:export', '导出咨询历史', 'button', 4, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (7, 'user:profile', '个人信息', 'menu', 0, 4, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (8, 'user:profile:edit', '编辑个人信息', 'button', 7, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (9, 'user:health_profile', '健康档案', 'menu', 0, 5, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (10, 'user:health_profile:edit', '编辑健康档案', 'button', 9, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (11, 'user:favorites', '我的收藏', 'menu', 0, 6, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (12, 'user:favorites:delete', '取消收藏', 'button', 11, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (13, 'user:settings', '系统设置', 'menu', 0, 7, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (20, 'admin:user', '用户管理', 'menu', 0, 10, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (21, 'admin:user:list', '用户列表', 'button', 20, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (22, 'admin:user:detail', '查看用户详情', 'button', 20, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (23, 'admin:user:disable', '禁用用户', 'button', 20, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (24, 'admin:user:view_history', '查看用户咨询历史', 'button', 20, 4, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (30, 'admin:review', '咨询内容审核', 'menu', 0, 11, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (31, 'admin:review:list', '审核列表', 'button', 30, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (32, 'admin:review:detail', '查看对话详情', 'button', 30, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (33, 'admin:review:mark', '标记违规内容', 'button', 30, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (34, 'admin:review:modify', '修改AI回复', 'button', 30, 4, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (35, 'admin:review:export', '导出审核记录', 'button', 30, 5, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (40, 'admin:faq', '常见问题管理', 'menu', 0, 12, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (41, 'admin:faq:create', '新增问题', 'button', 40, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (42, 'admin:faq:edit', '编辑问题', 'button', 40, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (43, 'admin:faq:delete', '删除问题', 'button', 40, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (44, 'admin:faq:sort', '调整排序', 'button', 40, 4, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (50, 'admin:config', '系统配置管理', 'menu', 0, 13, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (51, 'admin:config:edit', '修改配置', 'button', 50, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (60, 'admin:statistics', '数据统计看板', 'menu', 0, 14, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (61, 'admin:statistics:view', '查看统计', 'button', 60, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (62, 'admin:statistics:export', '导出统计', 'button', 60, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (70, 'admin:sensitive_word', '敏感词管理', 'menu', 0, 15, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (71, 'admin:sensitive_word:create', '新增敏感词', 'button', 70, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (72, 'admin:sensitive_word:edit', '编辑敏感词', 'button', 70, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (73, 'admin:sensitive_word:delete', '删除敏感词', 'button', 70, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (80, 'admin:manager', '管理员账号管理', 'menu', 0, 16, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (81, 'admin:manager:create', '新增管理员', 'button', 80, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (82, 'admin:manager:edit', '编辑管理员', 'button', 80, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (83, 'admin:manager:delete', '删除管理员', 'button', 80, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (90, 'admin:operation_log', '操作日志查看', 'menu', 0, 17, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `permissions` VALUES (91, 'admin:operation_log:view', '查看日志', 'button', 90, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for refund_requests
-- ----------------------------
DROP TABLE IF EXISTS `refund_requests`;
CREATE TABLE `refund_requests`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '退款原因',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0=待审核，1=已通过，2=已拒绝',
  `handled_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `handle_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核备注',
  `handled_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0=否，1=是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `fk_refund_requests_handled_by`(`handled_by` ASC) USING BTREE,
  CONSTRAINT `fk_refund_requests_handled_by` FOREIGN KEY (`handled_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_refund_requests_order_id` FOREIGN KEY (`order_id`) REFERENCES `payment_orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_refund_requests_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund_requests
-- ----------------------------
INSERT INTO `refund_requests` VALUES (3, 3, 2, '用户主动退款', 19.90, 0, NULL, NULL, NULL, '2026-05-25 15:46:42', '2026-05-25 15:46:42', 0);
INSERT INTO `refund_requests` VALUES (4, 8, 2, '用户主动退款', 69.90, 0, NULL, NULL, NULL, '2026-05-25 15:48:42', '2026-05-25 15:48:42', 0);

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `perm_id` bigint UNSIGNED NOT NULL COMMENT '权限ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id` ASC, `perm_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_perm_id`(`perm_id` ASC) USING BTREE,
  CONSTRAINT `fk_role_permissions_perm_id` FOREIGN KEY (`perm_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_permissions_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES (1, 1, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (2, 1, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (3, 1, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (4, 1, 4, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (5, 1, 5, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (6, 1, 6, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (7, 1, 7, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (8, 1, 8, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (9, 1, 9, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (10, 1, 10, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (11, 1, 11, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (12, 1, 12, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (13, 1, 13, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (14, 2, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (15, 2, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (16, 2, 4, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (17, 2, 7, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (18, 2, 9, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (19, 2, 11, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (20, 2, 13, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (21, 2, 20, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (22, 2, 30, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (23, 2, 40, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (24, 2, 50, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (25, 2, 60, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (26, 2, 70, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (27, 2, 80, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (28, 2, 90, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (29, 2, 3, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (30, 2, 5, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (31, 2, 6, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (32, 2, 8, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (33, 2, 10, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (34, 2, 12, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (35, 2, 21, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (36, 2, 22, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (37, 2, 23, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (38, 2, 24, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (39, 2, 31, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (40, 2, 32, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (41, 2, 33, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (42, 2, 34, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (43, 2, 35, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (44, 2, 41, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (45, 2, 42, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (46, 2, 43, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (47, 2, 44, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (48, 2, 51, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (49, 2, 61, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (50, 2, 62, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (51, 2, 71, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (52, 2, 72, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (53, 2, 73, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (54, 2, 81, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (55, 2, 82, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (56, 2, 83, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (57, 2, 91, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (77, 3, 20, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (78, 3, 21, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (79, 3, 22, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (80, 3, 23, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (81, 3, 24, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (82, 3, 50, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (83, 3, 51, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (84, 3, 60, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (85, 3, 61, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (86, 3, 62, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (87, 3, 70, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (88, 3, 71, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (89, 3, 72, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (90, 3, 73, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (91, 3, 90, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (92, 3, 91, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (93, 4, 30, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (94, 4, 31, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (95, 4, 32, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (96, 4, 33, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (97, 4, 34, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (98, 4, 35, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (99, 4, 40, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (100, 4, 41, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (101, 4, 42, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (102, 4, 43, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `role_permissions` VALUES (103, 4, 44, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码: R001=普通用户,R002=超级管理员,R003=系统管理员,R004=内容审核员',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '角色描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'R001', '普通用户', '使用系统进行健康咨询的C端用户', 1, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `roles` VALUES (2, 'R002', '超级管理员', '拥有系统所有操作权限', 2, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `roles` VALUES (3, 'R003', '系统管理员', '负责用户管理、系统配置、数据统计和运维工作', 3, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `roles` VALUES (4, 'R004', '内容审核员', '负责咨询内容审核、常见问题管理和违规内容处理', 4, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for sensitive_words
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_words`;
CREATE TABLE `sensitive_words`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '敏感词ID',
  `word` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '分类: 政治/色情/暴力/医疗风险/违法',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_word`(`word` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '敏感词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sensitive_words
-- ----------------------------
INSERT INTO `sensitive_words` VALUES (1, '毒品', '违法', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (2, '枪支', '违法', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (3, '赌博', '违法', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (4, '色情', '色情', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (5, '裸体', '色情', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (6, '自杀', '暴力', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (7, '杀人', '暴力', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (8, '处方', '医疗风险', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (9, '开药', '医疗风险', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (10, '诊断书', '医疗风险', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (11, '手术方案', '医疗风险', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `sensitive_words` VALUES (12, '特效药', '医疗风险', 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for sessions
-- ----------------------------
DROP TABLE IF EXISTS `sessions`;
CREATE TABLE `sessions`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '会话标题(首条问题前20字)',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `last_message_at` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1=进行中, 0=已结束',
  `category` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '\'咨询分类(内科/外科/儿科/...由AI自动识别)\'',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_last_msg`(`user_id` ASC, `last_message_at` ASC) USING BTREE,
  INDEX `idx_last_message_at`(`last_message_at` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  CONSTRAINT `fk_sessions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '咨询会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sessions
-- ----------------------------
INSERT INTO `sessions` VALUES (1, 2, '中午好', 10, '2026-05-21 10:42:34', 0, NULL, '2026-05-21 10:27:45', '2026-05-21 10:27:45', 0);
INSERT INTO `sessions` VALUES (2, 2, '如何改善睡眠', 2, '2026-05-21 10:34:18', 0, NULL, '2026-05-21 10:34:17', '2026-05-21 10:34:17', 0);
INSERT INTO `sessions` VALUES (3, 2, '头痛是什么原因', 4, '2026-05-21 11:31:14', 0, NULL, '2026-05-21 10:53:25', '2026-05-21 10:53:25', 0);
INSERT INTO `sessions` VALUES (4, 4, '如何改善睡眠', 6, '2026-05-26 11:00:36', 0, NULL, '2026-05-26 10:55:46', '2026-05-26 10:55:46', 0);
INSERT INTO `sessions` VALUES (5, 4, '皮肤过敏处理', 2, '2026-05-26 11:00:59', 0, NULL, '2026-05-26 11:00:48', '2026-05-26 11:00:48', 0);
INSERT INTO `sessions` VALUES (6, 4, '感冒发烧怎么办', 4, '2026-05-26 11:12:57', 0, NULL, '2026-05-26 11:10:14', '2026-05-26 11:10:14', 0);
INSERT INTO `sessions` VALUES (7, 5, '头痛是什么原因', 4, '2026-05-27 08:25:13', 0, NULL, '2026-05-27 08:24:51', '2026-05-27 08:24:51', 0);
INSERT INTO `sessions` VALUES (8, 5, '头痛是什么原因', 2, '2026-05-27 12:06:52', 0, NULL, '2026-05-27 12:06:50', '2026-05-27 12:06:50', 0);
INSERT INTO `sessions` VALUES (9, 2, '皮肤过敏处理', 2, '2026-05-28 11:30:53', 0, NULL, '2026-05-28 11:30:39', '2026-05-28 11:30:39', 0);
INSERT INTO `sessions` VALUES (10, 2, '血压高怎么办', 2, '2026-05-28 12:30:42', 0, NULL, '2026-05-28 12:28:27', '2026-05-28 12:28:27', 0);
INSERT INTO `sessions` VALUES (11, 2, '头痛是什么原因', 2, '2026-05-28 13:33:43', 0, NULL, '2026-05-28 13:33:29', '2026-05-28 13:33:29', 0);

-- ----------------------------
-- Table structure for sms_codes
-- ----------------------------
DROP TABLE IF EXISTS `sms_codes`;
CREATE TABLE `sms_codes`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `phone_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号SHA-256哈希',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint NULL DEFAULT 0 COMMENT '是否已使用',
  `send_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '发送IP',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone_hash`(`phone_hash` ASC) USING BTREE,
  INDEX `idx_phone_expire`(`phone_hash` ASC, `expire_time` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_codes
-- ----------------------------
INSERT INTO `sms_codes` VALUES (1, '64e1ec8107973099d85c38a20bdb8c67e3141edf7eb8faf73536ee1b7706aa55', '488692', '2026-05-20 08:43:05', 0, '127.0.0.1', '2026-05-20 08:38:05', 0);
INSERT INTO `sms_codes` VALUES (2, '64e1ec8107973099d85c38a20bdb8c67e3141edf7eb8faf73536ee1b7706aa55', '467243', '2026-05-20 08:47:24', 0, '127.0.0.1', '2026-05-20 08:42:24', 0);
INSERT INTO `sms_codes` VALUES (3, 'c664fc6fa3408f042ba1f124fc767b056f622887fc70540c72b7864a7d0562af', '448705', '2026-05-20 08:49:10', 0, '127.0.0.1', '2026-05-20 08:44:10', 0);
INSERT INTO `sms_codes` VALUES (4, 'cc05541ad895bbae30a38bc9559b68f27ddee65589472f5a6a035025c2f0f57a', '331033', '2026-05-21 09:46:55', 0, '127.0.0.1', '2026-05-21 09:41:55', 0);
INSERT INTO `sms_codes` VALUES (5, 'cc05541ad895bbae30a38bc9559b68f27ddee65589472f5a6a035025c2f0f57a', '614043', '2026-05-21 09:48:36', 0, '127.0.0.1', '2026-05-21 09:43:36', 0);
INSERT INTO `sms_codes` VALUES (6, 'cc05541ad895bbae30a38bc9559b68f27ddee65589472f5a6a035025c2f0f57a', '339719', '2026-05-21 09:58:12', 0, '127.0.0.1', '2026-05-21 09:53:12', 0);
INSERT INTO `sms_codes` VALUES (7, '63d2e3b15e9b218b50c9ef0dd1d8868e3db9fe7eb076700dc5f2c97a9d1ab22a', '673313', '2026-05-21 11:47:40', 0, '127.0.0.1', '2026-05-21 11:42:40', 0);
INSERT INTO `sms_codes` VALUES (8, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', '782164', '2026-05-26 11:00:12', 0, '127.0.0.1', '2026-05-26 10:55:12', 0);
INSERT INTO `sms_codes` VALUES (9, '64e1ec8107973099d85c38a20bdb8c67e3141edf7eb8faf73536ee1b7706aa55', '906545', '2026-05-27 08:29:14', 0, '10.47.164.193', '2026-05-27 08:24:14', 0);
INSERT INTO `sms_codes` VALUES (10, '64e1ec8107973099d85c38a20bdb8c67e3141edf7eb8faf73536ee1b7706aa55', '152877', '2026-05-27 08:29:14', 0, '10.47.164.193', '2026-05-27 08:24:14', 0);
INSERT INTO `sms_codes` VALUES (11, '64e1ec8107973099d85c38a20bdb8c67e3141edf7eb8faf73536ee1b7706aa55', '773753', '2026-05-27 08:34:43', 0, '10.47.164.193', '2026-05-27 08:29:43', 0);
INSERT INTO `sms_codes` VALUES (12, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', '190829', '2026-05-27 11:25:43', 0, '0:0:0:0:0:0:0:1', '2026-05-27 11:20:43', 0);
INSERT INTO `sms_codes` VALUES (13, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', '789521', '2026-05-27 11:40:58', 0, '0:0:0:0:0:0:0:1', '2026-05-27 11:35:58', 0);
INSERT INTO `sms_codes` VALUES (14, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', '311188', '2026-05-27 11:43:16', 0, '10.47.164.184', '2026-05-27 11:38:16', 0);
INSERT INTO `sms_codes` VALUES (15, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', '084234', '2026-05-28 09:00:35', 0, '0:0:0:0:0:0:0:1', '2026-05-28 08:55:35', 0);
INSERT INTO `sms_codes` VALUES (16, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', '615539', '2026-05-28 10:16:13', 0, '0:0:0:0:0:0:0:1', '2026-05-28 10:11:13', 0);

-- ----------------------------
-- Table structure for subscription_plans
-- ----------------------------
DROP TABLE IF EXISTS `subscription_plans`;
CREATE TABLE `subscription_plans`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案编码：L1_MONTHLY/L2_MONTHLY/L3_MONTHLY等',
  `plan_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方案名称：白银月卡/黄金月卡等',
  `level_code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对应会员等级：L1/L2/L3',
  `duration_days` int NOT NULL COMMENT '有效期天数',
  `price` decimal(10, 2) NOT NULL COMMENT '标准价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '首购体验价（仅首购用户可享）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重（升序）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0=下架，1=上架',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0=否，1=是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_plan_code`(`plan_code` ASC) USING BTREE,
  INDEX `fk_subscription_plans_level_code`(`level_code` ASC) USING BTREE,
  CONSTRAINT `fk_subscription_plans_level_code` FOREIGN KEY (`level_code`) REFERENCES `member_levels` (`level_code`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订阅方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subscription_plans
-- ----------------------------
INSERT INTO `subscription_plans` VALUES (1, 'L1_MONTHLY', '白银月卡', 'L1', 30, 19.90, 0.10, 1, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (2, 'L1_QUARTERLY', '白银季卡', 'L1', 90, 49.90, NULL, 2, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (3, 'L1_YEARLY', '白银年卡', 'L1', 365, 169.00, NULL, 3, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (4, 'L2_MONTHLY', '黄金月卡', 'L2', 30, 39.90, 9.90, 4, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (5, 'L2_QUARTERLY', '黄金季卡', 'L2', 90, 99.90, NULL, 5, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (6, 'L2_YEARLY', '黄金年卡', 'L2', 365, 349.00, NULL, 6, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (7, 'L3_MONTHLY', '铂金月卡', 'L3', 30, 69.90, 19.90, 7, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (8, 'L3_QUARTERLY', '铂金季卡', 'L3', 90, 179.00, NULL, 8, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);
INSERT INTO `subscription_plans` VALUES (9, 'L3_YEARLY', '铂金年卡', 'L3', 365, 599.00, NULL, 9, 1, '2026-05-20 00:34:38', '2026-05-20 00:34:38', 0);

-- ----------------------------
-- Table structure for system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'string' COMMENT '值类型: string/number/json/encrypted',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '配置说明',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_configs
-- ----------------------------
INSERT INTO `system_configs` VALUES (1, 'deepseek.api.url', 'https://api.deepseek.com/v1/chat/completions', 'string', 'DeepSeek API地址', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (2, 'deepseek.api.key', '', 'encrypted', 'DeepSeek API密钥', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (3, 'deepseek.model.version', 'deepseek-chat', 'string', 'DeepSeek模型版本', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (4, 'deepseek.temperature', '0.7', 'number', '模型温度参数', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (5, 'deepseek.max_tokens', '2048', 'number', '模型最大Token数', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (6, 'deepseek.top_p', '0.9', 'number', '模型Top-P参数', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (7, 'deepseek.retry.times', '3', 'number', 'API调用失败重试次数', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (8, 'sms.api.key', '', 'encrypted', '短信API密钥', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (9, 'sms.template.id', '', 'string', '短信模板ID', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (10, 'sms.code.expire.minutes', '5', 'number', '验证码有效期(分钟)', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (11, 'sms.code.send.interval', '60', 'number', '验证码发送间隔(秒)', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (12, 'rate.limit.per.minute', '60', 'number', '单用户每分钟最大咨询次数', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (13, 'system.prompt', '你是东软智慧健康咨询助手，仅能提供健康科普和咨询服务。\n严禁提供任何疾病诊断、治疗方案、处方建议或手术建议。\n所有回复开头必须添加：\"【健康咨询仅供参考，不能替代专业医生诊断和治疗】\"\n如用户描述紧急医疗情况(心梗、中风、大出血、呼吸困难等)，请立即回复：\"⚠️ 您描述的情况可能危及生命，请立即拨打120急救电话！\"并提供基础急救常识。\n如用户询问超出健康咨询范围的问题，请礼貌拒绝回答。\n回答要简洁明了，通俗易懂，避免使用专业术语。', 'string', '系统提示词', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (14, 'disclaimer.text', '【健康咨询仅供参考，不能替代专业医生诊断和治疗】', 'string', '免责声明文本', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `system_configs` VALUES (15, 'emergency.keywords', '心梗,心肌梗死,中风,脑梗,大出血,猝死,呼吸困难,窒息,心脏骤停,休克', 'string', '紧急关键词列表', '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `user_favorites`;
CREATE TABLE `user_favorites`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `message_id` bigint UNSIGNED NOT NULL COMMENT '消息ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_message`(`user_id` ASC, `message_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_user_favorites_message_id`(`message_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_favorites_message_id` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_favorites_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorites
-- ----------------------------
INSERT INTO `user_favorites` VALUES (1, 5, 30, '2026-05-27 08:24:59', '2026-05-27 08:25:00', 1);
INSERT INTO `user_favorites` VALUES (3, 2, 16, '2026-05-28 12:27:23', '2026-05-28 12:27:23', 1);

-- ----------------------------
-- Table structure for user_health_profiles
-- ----------------------------
DROP TABLE IF EXISTS `user_health_profiles`;
CREATE TABLE `user_health_profiles`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `blood_type` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '血型: A/B/AB/O/未知',
  `allergies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '过敏史(AES加密)',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '既往病史(AES加密)',
  `medication_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用药史(AES加密)',
  `family_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '家族病史(AES加密)',
  `auto_sync` tinyint NULL DEFAULT 0 COMMENT '是否自动同步给AI',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_health_profiles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户健康档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_health_profiles
-- ----------------------------
INSERT INTO `user_health_profiles` VALUES (1, 4, 165.00, 100.00, 'O', '青霉素，头孢', NULL, NULL, NULL, 0, '2026-05-26 11:20:34', '2026-05-26 11:20:34', 0);
INSERT INTO `user_health_profiles` VALUES (2, 5, 188.00, 70.00, 'AB', 'nhb', 'lll', '4777', '44444', 0, '2026-05-27 08:25:52', '2026-05-27 08:25:52', 0);

-- ----------------------------
-- Table structure for user_login_logs
-- ----------------------------
DROP TABLE IF EXISTS `user_login_logs`;
CREATE TABLE `user_login_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `login_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'sms' COMMENT '登录方式: sms/wechat/alipay',
  `login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录IP',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'User-Agent',
  `login_result` tinyint NULL DEFAULT 1 COMMENT '登录结果: 1=成功, 0=失败',
  `fail_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '失败原因',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `idx_user_login`(`user_id` ASC, `created_time` ASC) USING BTREE,
  CONSTRAINT `fk_user_login_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_login_logs
-- ----------------------------
INSERT INTO `user_login_logs` VALUES (1, 2, 'sms', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:150.0) Gecko/20100101 Firefox/150.0', 1, '', '2026-05-21 09:43:52', 0);
INSERT INTO `user_login_logs` VALUES (2, 2, 'sms', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:150.0) Gecko/20100101 Firefox/150.0', 1, '', '2026-05-21 09:53:39', 0);
INSERT INTO `user_login_logs` VALUES (3, 2, 'password', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:150.0) Gecko/20100101 Firefox/150.0', 1, '', '2026-05-21 10:01:10', 0);
INSERT INTO `user_login_logs` VALUES (4, 3, 'sms', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:150.0) Gecko/20100101 Firefox/150.0', 1, '', '2026-05-21 11:43:08', 0);
INSERT INTO `user_login_logs` VALUES (5, 2, 'password', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:150.0) Gecko/20100101 Firefox/150.0', 1, '', '2026-05-21 11:55:02', 0);
INSERT INTO `user_login_logs` VALUES (6, 2, 'password', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-25 09:42:46', 0);
INSERT INTO `user_login_logs` VALUES (7, 2, 'password', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-26 10:00:14', 0);
INSERT INTO `user_login_logs` VALUES (8, 4, 'sms', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-26 10:55:27', 0);
INSERT INTO `user_login_logs` VALUES (9, 5, 'sms', '10.47.164.193', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', 1, '', '2026-05-27 08:24:42', 0);
INSERT INTO `user_login_logs` VALUES (10, 5, 'password', '10.47.164.193', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', 1, '', '2026-05-27 08:42:42', 0);
INSERT INTO `user_login_logs` VALUES (11, 2, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 wechatdevtools/2.01.2510290 MicroMessenger/8.0.5 Language/zh_CN webview/ hash/1365560031 sid/FbilyfXpzG', 1, '', '2026-05-27 10:08:31', 0);
INSERT INTO `user_login_logs` VALUES (12, 4, 'sms', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', 1, '', '2026-05-27 11:21:14', 0);
INSERT INTO `user_login_logs` VALUES (13, 4, 'sms', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', 1, '', '2026-05-27 11:36:51', 0);
INSERT INTO `user_login_logs` VALUES (14, 4, 'sms', '10.47.164.184', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', 1, '', '2026-05-27 11:38:29', 0);
INSERT INTO `user_login_logs` VALUES (15, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:02:40', 0);
INSERT INTO `user_login_logs` VALUES (16, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:03:01', 0);
INSERT INTO `user_login_logs` VALUES (17, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:03:15', 0);
INSERT INTO `user_login_logs` VALUES (18, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:05:36', 0);
INSERT INTO `user_login_logs` VALUES (19, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:05:53', 0);
INSERT INTO `user_login_logs` VALUES (20, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:21:43', 0);
INSERT INTO `user_login_logs` VALUES (21, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 08:33:04', 0);
INSERT INTO `user_login_logs` VALUES (22, 4, 'sms', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', 1, '', '2026-05-28 08:56:02', 0);
INSERT INTO `user_login_logs` VALUES (23, 2, 'password', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 09:34:40', 0);
INSERT INTO `user_login_logs` VALUES (24, 4, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', 1, '', '2026-05-28 10:11:22', 0);
INSERT INTO `user_login_logs` VALUES (25, 2, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', 1, '', '2026-05-28 10:22:02', 0);
INSERT INTO `user_login_logs` VALUES (26, 4, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36', 1, '', '2026-05-28 10:45:43', 0);
INSERT INTO `user_login_logs` VALUES (27, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:151.0) Gecko/20100101 Firefox/151.0', 1, '', '2026-05-28 12:36:26', 0);
INSERT INTO `user_login_logs` VALUES (28, 1, 'password', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) TraeCN/1.107.1 Chrome/142.0.7444.235 Electron/39.2.7 Safari/537.36', 1, '', '2026-05-28 14:23:56', 0);

-- ----------------------------
-- Table structure for user_memberships
-- ----------------------------
DROP TABLE IF EXISTS `user_memberships`;
CREATE TABLE `user_memberships`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `level_code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会员等级：L1/L2/L3',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `expire_time` datetime NOT NULL COMMENT '到期时间',
  `grace_end_time` datetime NULL DEFAULT NULL COMMENT '宽限期截止时间（到期后+24h）',
  `source_order_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '来源订单ID（管理员开通则为NULL）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0=失效，1=生效',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除：0=否，1=是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE,
  INDEX `fk_user_memberships_level_code`(`level_code` ASC) USING BTREE,
  INDEX `fk_user_memberships_order_id`(`source_order_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_memberships_level_code` FOREIGN KEY (`level_code`) REFERENCES `member_levels` (`level_code`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_user_memberships_order_id` FOREIGN KEY (`source_order_id`) REFERENCES `payment_orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_user_memberships_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_memberships
-- ----------------------------
INSERT INTO `user_memberships` VALUES (1, 2, 'L1', '2026-05-25 10:20:51', '2026-06-24 10:20:51', NULL, 2, 1, '2026-05-25 10:20:51', '2026-05-25 10:20:51', 0);
INSERT INTO `user_memberships` VALUES (2, 2, 'L1', '2026-05-25 10:20:51', '2026-06-24 10:20:51', NULL, 2, 1, '2026-05-25 10:20:52', '2026-05-25 10:20:52', 0);
INSERT INTO `user_memberships` VALUES (3, 2, 'L1', '2026-05-25 10:21:02', '2026-07-24 10:20:51', NULL, 3, 1, '2026-05-25 10:21:02', '2026-05-25 10:21:02', 0);
INSERT INTO `user_memberships` VALUES (4, 2, 'L3', '2026-05-25 11:14:50', '2026-08-23 10:20:51', NULL, 8, 1, '2026-05-25 11:14:50', '2026-05-25 11:14:50', 0);
INSERT INTO `user_memberships` VALUES (5, 4, 'L3', '2026-05-26 11:58:50', '2027-05-26 11:58:50', NULL, 21, 1, '2026-05-26 11:58:50', '2026-05-26 11:58:50', 0);
INSERT INTO `user_memberships` VALUES (6, 4, 'L3', '2026-05-26 12:06:37', '2028-05-25 11:58:50', NULL, 25, 1, '2026-05-26 12:06:37', '2026-05-26 12:06:37', 0);
INSERT INTO `user_memberships` VALUES (7, 2, 'L1', '2026-05-27 10:49:13', '2026-09-22 10:20:51', NULL, 28, 1, '2026-05-27 10:49:13', '2026-05-27 10:49:13', 0);

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (1, 1, 2, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);

-- ----------------------------
-- Table structure for user_settings
-- ----------------------------
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `notification_enabled` tinyint NULL DEFAULT 1 COMMENT '通知开关',
  `voice_enabled` tinyint NULL DEFAULT 1 COMMENT '语音播报开关：0=关闭，1=开启',
  `voice_speed` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '语音播报语速',
  `voice_volume` int NULL DEFAULT 80 COMMENT '语音音量',
  `voice_tone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'default' COMMENT '语音音色',
  `anonymous_mode` tinyint NULL DEFAULT 0 COMMENT '匿名咨询模式',
  `privacy_mode` tinyint NULL DEFAULT 0 COMMENT '隐私模式：0=关闭，1=开启',
  `recommend_enabled` tinyint NULL DEFAULT 1 COMMENT '是否允许推荐',
  `auto_sync_health_profile` tinyint NULL DEFAULT 1 COMMENT '健康档案自动同步：0=关闭，1=开启',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_settings_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_settings
-- ----------------------------
INSERT INTO `user_settings` VALUES (1, 1, 1, 1, 1.00, 80, 'default', 0, 0, 1, 1, '2026-05-19 23:07:56', '2026-05-19 23:07:56', 0);
INSERT INTO `user_settings` VALUES (2, 2, 0, 1, 0.50, 80, 'default', 0, 0, 1, 1, '2026-05-25 11:48:50', '2026-05-25 11:48:50', 0);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号SHA-256哈希，用于唯一索引',
  `phone_encrypted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号AES-256加密',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'BCrypt密码哈希（可选）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '昵称',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别: 0=未知, 1=男, 2=女',
  `age` int NULL DEFAULT 0 COMMENT '年龄',
  `open_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '第三方登录OpenID',
  `login_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'sms' COMMENT '注册方式: sms/wechat/alipay',
  `disclaimer_accepted` tinyint NULL DEFAULT 0 COMMENT '是否已确认免责声明',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0=否, 1=是',
  `member_level` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'L0' COMMENT '当前会员等级',
  `member_expire_time` datetime NULL DEFAULT NULL COMMENT '会员到期时间',
  `invite_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邀请码(8位唯一)',
  `invited_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '邀请人ID',
  `first_purchase` tinyint NULL DEFAULT 0 COMMENT '是否已完成首购(0=否 1=是)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone_hash`(`phone_hash` ASC) USING BTREE,
  UNIQUE INDEX `uk_invite_code`(`invite_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE,
  INDEX `idx_open_id`(`open_id` ASC) USING BTREE,
  INDEX `idx_invited_by`(`invited_by` ASC) USING BTREE,
  CONSTRAINT `fk_users_invited_by` FOREIGN KEY (`invited_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '359ea74a80a57accd42a7311ed96eca04f3e631d0ab34ea76808c543240d8a68', '13800000000', '$2a$10$OTxOXs.eVcGdhQbfz3qv/u6W2TWPHOKa8jQMU9V3X8//CZbg7xKO6', '超级管理员', '', 0, 0, '', 'sms', 1, '2026-05-28 14:23:56', 1, '2026-05-19 23:07:56', '2026-05-21 09:57:13', 0, 'L0', NULL, NULL, NULL, 0);
INSERT INTO `users` VALUES (2, 'cc05541ad895bbae30a38bc9559b68f27ddee65589472f5a6a035025c2f0f57a', '/cRyQAxde/XAmC3umURs7w==', '$2a$10$OTxOXs.eVcGdhQbfz3qv/u6W2TWPHOKa8jQMU9V3X8//CZbg7xKO6', '用户31972', '', 0, 0, '', 'sms', 0, '2026-05-28 10:22:02', 1, '2026-05-21 09:43:52', '2026-05-26 10:53:40', 0, 'L1', '2026-09-22 10:20:51', '1F1CD66F', NULL, 1);
INSERT INTO `users` VALUES (3, '63d2e3b15e9b218b50c9ef0dd1d8868e3db9fe7eb076700dc5f2c97a9d1ab22a', 'm057Tbas0akmj8J0X1b19w==', NULL, '用户88477', '', 0, 0, '', 'sms', 0, NULL, 1, '2026-05-21 11:43:08', '2026-05-21 11:54:45', 0, 'L0', NULL, 'A10F9447', NULL, 0);
INSERT INTO `users` VALUES (4, '96f12463f0d4d98da558520c77bb6edba3afb9ffdfadf9ebda85b3051cb7c5ef', 'lznKLw4Q6wao9eqVkoO3cw==', '$2a$10$OTxOXs.eVcGdhQbfz3qv/u6W2TWPHOKa8jQMU9V3X8//CZbg7xKO6', '用户27103', '', 0, 0, '', 'sms', 0, NULL, 1, '2026-05-26 10:55:27', '2026-05-28 10:50:12', 0, 'L3', '2028-05-25 11:58:50', 'F801334A', NULL, 1);
INSERT INTO `users` VALUES (5, '64e1ec8107973099d85c38a20bdb8c67e3141edf7eb8faf73536ee1b7706aa55', 'EQtWIYZnXKfaV4vx1xWBnw==', NULL, '用户81048', '', 0, 0, '', 'sms', 0, '2026-05-27 08:42:41', 1, '2026-05-27 08:24:41', '2026-05-27 11:54:13', 0, 'L0', NULL, 'D859846C', NULL, 0);

SET FOREIGN_KEY_CHECKS = 1;
