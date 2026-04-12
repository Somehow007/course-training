-- 操作日志表
-- 用于记录用户操作，包括密码重置等敏感操作

CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` bigint NOT NULL COMMENT '主键ID（Snowflake）',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(128) NOT NULL COMMENT '操作人用户名',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型（如：RESET_PASSWORD）',
  `operation_desc` varchar(255) DEFAULT NULL COMMENT '操作描述',
  `target_user_id` bigint DEFAULT NULL COMMENT '目标用户ID',
  `target_username` varchar(128) DEFAULT NULL COMMENT '目标用户名',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '操作IP地址',
  `result` tinyint(1) DEFAULT '1' COMMENT '操作结果（1:成功 0:失败）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识（0:未删除 1:删除）',
  PRIMARY KEY (`id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_target_user_id` (`target_user_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';
