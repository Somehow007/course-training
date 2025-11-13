-- 删除旧表（如果存在）
DROP TABLE IF EXISTS major_category_config;

-- 创建新表
CREATE TABLE major_category_config (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID（自增主键，供 major 表外键引用）',
                                       discipline_category VARCHAR(50) NOT NULL COMMENT '学科门类（如“工学”、“理学”、“文学”）',
                                       professional_category VARCHAR(50) NOT NULL COMMENT '专业类别（如“计算机类”、“数学类”、“中国语言文学类”）',
                                       description VARCHAR(200) NULL COMMENT '描述（可选，如“含人工智能、大数据方向”）',
                                       sort_order INT DEFAULT 0 COMMENT '排序值（数值小的排前面，便于前端展示顺序）',
                                       is_enabled TINYINT DEFAULT 1 COMMENT '是否启用（0=禁用，1=启用）',
                                       created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    --  防止重复录入相同的“学科门类 + 专业类别”
                                       UNIQUE KEY uk_discipline_professional (discipline_category, professional_category),

    --  常用索引
                                       INDEX idx_enabled (is_enabled),
                                       INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业分类配置表（管理员只需填中文，系统自动生成ID）';

--  插入教育部主流学科分类初始数据（你可以根据最新目录调整）
INSERT INTO major_category_config (discipline_category, professional_category, sort_order, description) VALUES
                                                                                                            ('工学', '计算机类', 10, '含计算机科学与技术、软件工程、网络工程等'),
                                                                                                            ('工学', '软件工程类', 20, '含软件工程、信息安全、数字媒体技术等'),
                                                                                                            ('工学', '机械类', 30, '含机械设计制造及其自动化、车辆工程等'),
                                                                                                            ('工学', '电子信息类', 40, '含电子信息工程、通信工程、人工智能等'),
                                                                                                            ('工学', '土木类', 50, '含土木工程、建筑环境与能源应用工程等'),

                                                                                                            ('理学', '数学类', 60, '含数学与应用数学、信息与计算科学等'),
                                                                                                            ('理学', '物理学类', 70, '含物理学、应用物理学等'),
                                                                                                            ('理学', '化学类', 80, '含化学、应用化学等'),
                                                                                                            ('理学', '生物科学类', 90, '含生物科学、生物技术等'),

                                                                                                            ('文学', '中国语言文学类', 100, '含汉语言文学、汉语国际教育等'),
                                                                                                            ('文学', '外国语言文学类', 110, '含英语、日语、翻译等'),
                                                                                                            ('文学', '新闻传播学类', 120, '含新闻学、广告学、传播学等'),

                                                                                                            ('法学', '法学类', 130, '含法学、知识产权等'),
                                                                                                            ('法学', '政治学类', 140, '含政治学与行政学、国际政治等'),

                                                                                                            ('经济学', '经济学类', 150, '含经济学、经济统计学等'),
                                                                                                            ('管理学', '管理科学与工程类', 160, '含信息管理与信息系统、工程管理等'),

                                                                                                            ('艺术学', '设计学类', 170, '含视觉传达设计、环境设计、产品设计等'),
                                                                                                            ('艺术学', '美术学类', 180, '含绘画、雕塑、美术学等'),

                                                                                                            ('教育学', '教育学类', 190, '含教育学、科学教育、学前教育等'),
                                                                                                            ('教育学', '心理学类', 200, '含心理学、应用心理学等'),

                                                                                                            ('历史学', '历史学类', 210, '含历史学、世界史、考古学等'),
                                                                                                            ('哲学', '哲学类', 220, '含哲学、逻辑学、宗教学等'),

-- ✅ 可选：预留“交叉学科”或“新兴专业”
                                                                                                            ('工学', '人工智能类', 25, '含人工智能、智能科学与技术、机器人工程等'),
                                                                                                            ('理学', '数据科学类', 65, '含数据科学与大数据技术、统计学等');