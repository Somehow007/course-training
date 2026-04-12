-- 版本管理数据库表设计
-- 为培养方案管理系统添加版本管理功能

-- 1. 版本历史表
DROP TABLE IF EXISTS `version_history`;
CREATE TABLE `version_history` (
  `id` bigint NOT NULL COMMENT '版本ID（Snowflake）',
  `training_program_id` bigint NOT NULL COMMENT '培养方案ID',
  `version_number` int NOT NULL COMMENT '版本号',
  `version_name` varchar(100) NOT NULL COMMENT '版本名称',
  `version_status` tinyint NOT NULL DEFAULT 0 COMMENT '版本状态（0:草稿 1:已发布 2:已归档 3:已回滚）',
  `change_description` text COMMENT '变更说明',
  `snapshot_data` longtext COMMENT '版本快照数据（JSON格式）',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `creator_name` varchar(50) NOT NULL COMMENT '创建人姓名',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `publish_user_id` bigint DEFAULT NULL COMMENT '发布人ID',
  `publish_user_name` varchar(50) DEFAULT NULL COMMENT '发布人姓名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint DEFAULT 0 COMMENT '删除标识（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_training_program_id` (`training_program_id`),
  KEY `idx_version_number` (`version_number`),
  KEY `idx_version_status` (`version_status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_version_training_program` FOREIGN KEY (`training_program_id`) REFERENCES `training_program` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='版本历史表';

-- 2. 版本变更记录表
DROP TABLE IF EXISTS `version_change_log`;
CREATE TABLE `version_change_log` (
  `id` bigint NOT NULL COMMENT '变更记录ID（Snowflake）',
  `version_id` bigint NOT NULL COMMENT '版本ID',
  `change_type` tinyint NOT NULL COMMENT '变更类型（1:新增 2:修改 3:删除）',
  `change_target` varchar(50) NOT NULL COMMENT '变更对象（course:课程 detail:课程详情）',
  `target_id` bigint DEFAULT NULL COMMENT '变更对象ID',
  `old_value` text COMMENT '变更前的值（JSON格式）',
  `new_value` text COMMENT '变更后的值（JSON格式）',
  `change_description` varchar(500) COMMENT '变更描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_version_id` (`version_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_change_log_version` FOREIGN KEY (`version_id`) REFERENCES `version_history` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='版本变更记录表';

-- 3. 修改培养方案表，添加版本相关字段
ALTER TABLE `training_program` 
ADD COLUMN `current_version` int DEFAULT 1 COMMENT '当前版本号',
ADD COLUMN `version_status` tinyint DEFAULT 0 COMMENT '版本状态（0:草稿 1:已发布）',
ADD COLUMN `last_version_id` bigint DEFAULT NULL COMMENT '最新版本ID';

-- 4. 创建索引以提升查询性能
CREATE INDEX `idx_current_version` ON `training_program` (`current_version`);
CREATE INDEX `idx_version_status` ON `training_program` (`version_status`);
CREATE INDEX `idx_last_version_id` ON `training_program` (`last_version_id`);

-- 5. 初始化数据（可选）
-- 为现有培养方案创建初始版本记录
INSERT INTO `version_history` (`id`, `training_program_id`, `version_number`, `version_name`, `version_status`, `change_description`, `creator_id`, `creator_name`, `create_time`)
SELECT 
  id as `id`,
  id as `training_program_id`,
  1 as `version_number`,
  CONCAT(name, ' - 初始版本') as `version_name`,
  1 as `version_status`,
  '系统初始化版本' as `change_description`,
  (SELECT id FROM `user` LIMIT 1) as `creator_id`,
  '系统管理员' as `creator_name`,
  create_time
FROM `training_program`
WHERE `del_flag` = 0;

-- 更新培养方案表的版本信息
UPDATE `training_program` tp
SET 
  `current_version` = 1,
  `version_status` = 1,
  `last_version_id` = tp.id
WHERE `del_flag` = 0;
