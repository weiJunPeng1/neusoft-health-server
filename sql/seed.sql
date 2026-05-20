-- ============================================================
-- 东软智慧健康咨询系统 V1.0 种子数据
-- 包含: 角色、权限、角色权限关联、FAQ分类、FAQ问题、系统配置、敏感词、超级管理员
-- ============================================================

USE neusoft_health;

-- ============================================================
-- 1. 角色初始化
-- ============================================================
INSERT INTO roles (id, role_code, role_name, description, sort_order, status) VALUES
(1, 'R001', '普通用户', '使用系统进行健康咨询的C端用户', 1, 1),
(2, 'R002', '超级管理员', '拥有系统所有操作权限', 2, 1),
(3, 'R003', '系统管理员', '负责用户管理、系统配置、数据统计和运维工作', 3, 1),
(4, 'R004', '内容审核员', '负责咨询内容审核、常见问题管理和违规内容处理', 4, 1);

-- ============================================================
-- 2. 权限初始化 (菜单权限 + 按钮权限 + 数据权限)
-- ============================================================
INSERT INTO permissions (id, perm_code, perm_name, perm_type, parent_id, sort_order) VALUES
-- 前台用户端
(1,  'user:dashboard',       '系统首页',          'menu',   0,  1),
(2,  'user:consultation',    'AI健康咨询',        'menu',   0,  2),
(3,  'user:consultation:send', '发送咨询消息',    'button', 2,  1),
(4,  'user:history',         '咨询历史',          'menu',   0,  3),
(5,  'user:history:delete',  '删除咨询历史',      'button', 4,  1),
(6,  'user:history:export',  '导出咨询历史',      'button', 4,  2),
(7,  'user:profile',         '个人信息',          'menu',   0,  4),
(8,  'user:profile:edit',    '编辑个人信息',      'button', 7,  1),
(9,  'user:health_profile',  '健康档案',          'menu',   0,  5),
(10, 'user:health_profile:edit', '编辑健康档案',  'button', 9,  1),
(11, 'user:favorites',       '我的收藏',          'menu',   0,  6),
(12, 'user:favorites:delete','取消收藏',          'button', 11, 1),
(13, 'user:settings',        '系统设置',          'menu',   0,  7),

-- 管理后台-用户管理
(20, 'admin:user',            '用户管理',         'menu',   0,  10),
(21, 'admin:user:list',       '用户列表',         'button', 20, 1),
(22, 'admin:user:detail',     '查看用户详情',     'button', 20, 2),
(23, 'admin:user:disable',    '禁用用户',         'button', 20, 3),
(24, 'admin:user:view_history','查看用户咨询历史', 'button', 20, 4),

-- 管理后台-内容审核
(30, 'admin:review',          '咨询内容审核',     'menu',   0,  11),
(31, 'admin:review:list',     '审核列表',         'button', 30, 1),
(32, 'admin:review:detail',   '查看对话详情',     'button', 30, 2),
(33, 'admin:review:mark',     '标记违规内容',     'button', 30, 3),
(34, 'admin:review:modify',   '修改AI回复',       'button', 30, 4),
(35, 'admin:review:export',   '导出审核记录',     'button', 30, 5),

-- 管理后台-常见问题管理
(40, 'admin:faq',             '常见问题管理',     'menu',   0,  12),
(41, 'admin:faq:create',      '新增问题',         'button', 40, 1),
(42, 'admin:faq:edit',        '编辑问题',         'button', 40, 2),
(43, 'admin:faq:delete',      '删除问题',         'button', 40, 3),
(44, 'admin:faq:sort',        '调整排序',         'button', 40, 4),

-- 管理后台-系统配置
(50, 'admin:config',          '系统配置管理',     'menu',   0,  13),
(51, 'admin:config:edit',     '修改配置',         'button', 50, 1),

-- 管理后台-数据统计
(60, 'admin:statistics',      '数据统计看板',     'menu',   0,  14),
(61, 'admin:statistics:view', '查看统计',         'button', 60, 1),
(62, 'admin:statistics:export','导出统计',        'button', 60, 2),

-- 管理后台-敏感词管理
(70, 'admin:sensitive_word',  '敏感词管理',       'menu',   0,  15),
(71, 'admin:sensitive_word:create', '新增敏感词', 'button', 70, 1),
(72, 'admin:sensitive_word:edit',   '编辑敏感词', 'button', 70, 2),
(73, 'admin:sensitive_word:delete', '删除敏感词', 'button', 70, 3),

-- 管理后台-管理员管理 (仅超级管理员)
(80, 'admin:manager',         '管理员账号管理',   'menu',   0,  16),
(81, 'admin:manager:create',  '新增管理员',       'button', 80, 1),
(82, 'admin:manager:edit',    '编辑管理员',       'button', 80, 2),
(83, 'admin:manager:delete',  '删除管理员',       'button', 80, 3),

-- 管理后台-操作日志
(90, 'admin:operation_log',   '操作日志查看',     'menu',   0,  17),
(91, 'admin:operation_log:view', '查看日志',      'button', 90, 1);

-- ============================================================
-- 3. 角色权限关联
-- ============================================================
-- R001 普通用户: 前台所有权限
INSERT INTO role_permissions (role_id, perm_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13);

-- R002 超级管理员: 所有权限
INSERT INTO role_permissions (role_id, perm_id)
SELECT 2, id FROM permissions;

-- R003 系统管理员: 用户管理 + 系统配置 + 数据统计 + 操作日志 + 敏感词管理 (不含管理员账号管理)
INSERT INTO role_permissions (role_id, perm_id) VALUES
(3, 20), (3, 21), (3, 22), (3, 23), (3, 24),
(3, 50), (3, 51),
(3, 60), (3, 61), (3, 62),
(3, 70), (3, 71), (3, 72), (3, 73),
(3, 90), (3, 91);

-- R004 内容审核员: 审核 + FAQ管理
INSERT INTO role_permissions (role_id, perm_id) VALUES
(4, 30), (4, 31), (4, 32), (4, 33), (4, 34), (4, 35),
(4, 40), (4, 41), (4, 42), (4, 43), (4, 44);

-- ============================================================
-- 4. FAQ分类初始化
-- ============================================================
INSERT INTO faq_categories (id, name, sort_order, status) VALUES
(1, '感冒发烧',   1, 1),
(2, '肠胃不适',   2, 1),
(3, '睡眠问题',   3, 1),
(4, '运动健康',   4, 1),
(5, '饮食营养',   5, 1),
(6, '皮肤问题',   6, 1);

-- ============================================================
-- 5. FAQ常见问题初始化 (每个分类至少5条)
-- ============================================================
INSERT INTO faqs (category_id, question, sort_order, status) VALUES
-- 感冒发烧
(1, '感冒了应该吃什么药？', 1, 1),
(1, '发烧多少度需要去医院？', 2, 1),
(1, '感冒和流感有什么区别？', 3, 1),
(1, '感冒了可以运动吗？', 4, 1),
(1, '小孩感冒发烧怎么办？', 5, 1),
(1, '感冒了多喝水真的有用吗？', 6, 1),

-- 肠胃不适
(2, '胃疼怎么缓解？', 1, 1),
(2, '拉肚子应该吃什么？', 2, 1),
(2, '经常胃胀气是什么原因？', 3, 1),
(2, '肠胃炎需要禁食吗？', 4, 1),
(2, '幽门螺杆菌感染怎么办？', 5, 1),

-- 睡眠问题
(3, '失眠怎么快速入睡？', 1, 1),
(3, '每天睡几个小时最健康？', 2, 1),
(3, '熬夜后怎么恢复？', 3, 1),
(3, '打鼾是病吗？', 4, 1),
(3, '褪黑素可以长期吃吗？', 5, 1),

-- 运动健康
(4, '每天走多少步最健康？', 1, 1),
(4, '跑步伤膝盖怎么办？', 2, 1),
(4, '运动后肌肉酸痛怎么缓解？', 3, 1),
(4, '什么时间运动效果最好？', 4, 1),
(4, '久坐的人应该做什么运动？', 5, 1),

-- 饮食营养
(5, '怎么吃才能减肥？', 1, 1),
(5, '每天吃水果的最佳时间？', 2, 1),
(5, '高血脂应该怎么饮食？', 3, 1),
(5, '素食主义会营养不良吗？', 4, 1),
(5, '补钙应该吃什么？', 5, 1),

-- 皮肤问题
(6, '长痘痘怎么消？', 1, 1),
(6, '皮肤干燥起皮怎么办？', 2, 1),
(6, '湿疹怎么治疗？', 3, 1),
(6, '防晒霜怎么选？', 4, 1),
(6, '皮肤过敏了怎么办？', 5, 1);

-- ============================================================
-- 6. 系统配置初始化
-- ============================================================
INSERT INTO system_configs (config_key, config_value, config_type, description) VALUES
('deepseek.api.url',        'https://api.deepseek.com/v1/chat/completions', 'string',    'DeepSeek API地址'),
('deepseek.api.key',        '',                                              'encrypted', 'DeepSeek API密钥'),
('deepseek.model.version',  'deepseek-chat',                                'string',    'DeepSeek模型版本'),
('deepseek.temperature',    '0.7',                                          'number',    '模型温度参数'),
('deepseek.max_tokens',     '2048',                                         'number',    '模型最大Token数'),
('deepseek.top_p',          '0.9',                                          'number',    '模型Top-P参数'),
('deepseek.retry.times',    '3',                                            'number',    'API调用失败重试次数'),
('sms.api.key',             '',                                              'encrypted', '短信API密钥'),
('sms.template.id',         '',                                              'string',    '短信模板ID'),
('sms.code.expire.minutes', '5',                                            'number',    '验证码有效期(分钟)'),
('sms.code.send.interval',  '60',                                           'number',    '验证码发送间隔(秒)'),
('rate.limit.per.minute',   '60',                                           'number',    '单用户每分钟最大咨询次数'),
('system.prompt',           '你是东软智慧健康咨询助手，仅能提供健康科普和咨询服务。\n严禁提供任何疾病诊断、治疗方案、处方建议或手术建议。\n所有回复开头必须添加："【健康咨询仅供参考，不能替代专业医生诊断和治疗】"\n如用户描述紧急医疗情况(心梗、中风、大出血、呼吸困难等)，请立即回复："⚠️ 您描述的情况可能危及生命，请立即拨打120急救电话！"并提供基础急救常识。\n如用户询问超出健康咨询范围的问题，请礼貌拒绝回答。\n回答要简洁明了，通俗易懂，避免使用专业术语。', 'string', '系统提示词'),
('disclaimer.text',         '【健康咨询仅供参考，不能替代专业医生诊断和治疗】', 'string',    '免责声明文本'),
('emergency.keywords',      '心梗,心肌梗死,中风,脑梗,大出血,猝死,呼吸困难,窒息,心脏骤停,休克', 'string', '紧急关键词列表');

-- ============================================================
-- 7. 敏感词初始化
-- ============================================================
INSERT INTO sensitive_words (word, category, status) VALUES
('毒品',      '违法', 1),
('枪支',      '违法', 1),
('赌博',      '违法', 1),
('色情',      '色情', 1),
('裸体',      '色情', 1),
('自杀',      '暴力', 1),
('杀人',      '暴力', 1),
('处方',      '医疗风险', 1),
('开药',      '医疗风险', 1),
('诊断书',    '医疗风险', 1),
('手术方案',  '医疗风险', 1),
('特效药',    '医疗风险', 1);

-- ============================================================
-- 8. 超级管理员初始化 (密码: admin123, BCrypt加密)
-- 手机号: 13800000000
-- ============================================================
INSERT INTO users (id, phone_hash, phone_encrypted, password_hash, nickname, gender, age, disclaimer_accepted, status)
VALUES (1,
    SHA2('13800000000', 256),
    '13800000000',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh',
    '超级管理员', 0, 0, 1, 1);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO user_settings (user_id) VALUES (1);
