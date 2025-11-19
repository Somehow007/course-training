-- -------------------------------------------------------------
-- TablePlus 6.4.2(600)
--
-- https://tableplus.com/
--
-- Database: course_training
-- Generation Time: 2025-11-19 21:42:57.2290
-- -------------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


DROP TABLE IF EXISTS `college`;
CREATE TABLE `college` (
  `id` bigint NOT NULL COMMENT '学院代理主键（Snowflake）',
  `college_code` char(2) NOT NULL COMMENT '学院编号（业务码，可变更）',
  `college_name` varchar(40) NOT NULL COMMENT '学院名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `college_code` (`college_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='学院表';

DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT '课程代理主键（Snowflake）',
  `course_code` varchar(20) DEFAULT NULL COMMENT '课程业务编码（如：COURSE_001）',
  `dict_id` bigint NOT NULL COMMENT '字典表ID（关联sys_dict.id）',
  `course_nature` tinyint NOT NULL COMMENT '课程性质（0:必修 1:选修）',
  `course_name` varchar(30) NOT NULL COMMENT '课程名',
  `college_id` bigint DEFAULT NULL COMMENT '开课学院ID（关联 college.id）',
  `major_id` bigint DEFAULT NULL COMMENT '修读专业ID（关联 major.id）',
  `total_credits` float(3,1) NOT NULL COMMENT '总学分',
  `total_hours` float(4,1) DEFAULT NULL COMMENT '总学时（小时）',
  `total_weeks` float(4,1) DEFAULT NULL COMMENT '总学时（周）',
  `hours_unit` tinyint(1) NOT NULL COMMENT '当前使用的学时单位（0:小时 1:周）',
  `hour_teach` int DEFAULT NULL COMMENT '授课学时',
  `hour_practice` int DEFAULT NULL COMMENT '实验学时',
  `hour_operation` int DEFAULT NULL COMMENT '上机学时',
  `hour_outside` int DEFAULT NULL COMMENT '实践学时',
  `hour_week` int DEFAULT NULL COMMENT '周学时',
  `term` tinyint(1) DEFAULT NULL COMMENT '建议修读学期',
  `required_elective` tinyint DEFAULT NULL COMMENT '选修学分要求',
  `course_choose_id` char(2) DEFAULT NULL COMMENT '选修计划ID',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识（0:未删除 1:删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `course_code` (`course_code`),
  KEY `fk_course_college` (`college_id`),
  KEY `fk_course_major` (`major_id`),
  KEY `fk_course_dict` (`dict_id`),
  CONSTRAINT `fk_course_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_course_dict` FOREIGN KEY (`dict_id`) REFERENCES `sys_dict` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_course_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程表';

DROP TABLE IF EXISTS `course_choose_plan`;
CREATE TABLE `course_choose_plan` (
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `course_exclusivity`;
CREATE TABLE `course_exclusivity` (
  `id` bigint NOT NULL COMMENT '分组代理主键（Snowflake）',
  `group_code` char(2) DEFAULT NULL COMMENT '多选组编码（业务码）',
  `required_credits` int unsigned DEFAULT NULL COMMENT '需选修够的学分',
  PRIMARY KEY (`id`),
  KEY `idx_exclusivity_group_code` (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='课程互斥/分组表';

DROP TABLE IF EXISTS `major`;
CREATE TABLE `major` (
  `id` bigint NOT NULL COMMENT '专业代理主键',
  `major_code` char(4) DEFAULT NULL COMMENT '专业代码（如 0809）',
  `major_name` varchar(40) NOT NULL COMMENT '专业名称',
  `college_id` bigint DEFAULT NULL COMMENT '所属学院ID',
  `course_num` int DEFAULT '0' COMMENT '课程总数',
  `category_id` tinyint(1) NOT NULL COMMENT '专业分类（0:工学 1:理学 2:文科）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_major_code` (`major_code`),
  KEY `idx_college_id` (`college_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_del_flag` (`del_flag`),
  CONSTRAINT `fk_major_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';

DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type` varchar(50) NOT NULL COMMENT '字典类型（如：course_type, user_state）',
  `dict_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '字典编码（程序内部引用，英文大写+下划线）',
  `dict_name` varchar(100) NOT NULL COMMENT '字典名称（用户界面显示）',
  `sort_order` int DEFAULT '0' COMMENT '排序号（控制下拉选项顺序）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type_code` (`dict_type`,`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1988934871961915393 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统字典表（用于管理枚举值）';

DROP TABLE IF EXISTS `tp_history`;
CREATE TABLE `tp_history` (
  `id` bigint NOT NULL COMMENT '历史记录代理主键（Snowflake）',
  `tp_id` bigint NOT NULL COMMENT '原始培养计划ID（用于关联）',
  `major_id` bigint NOT NULL COMMENT '关联专业ID',
  `college_id` bigint NOT NULL COMMENT '所属学院ID',
  `gmt_updated` datetime NOT NULL COMMENT '上传时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_tp_hist_college_id` (`college_id`),
  KEY `idx_tp_hist_major_id` (`major_id`),
  KEY `idx_tp_hist_tp_id` (`tp_id`),
  CONSTRAINT `fk_tp_hist_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_tp_hist_major` FOREIGN KEY (`major_id`) REFERENCES `major` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_tp_hist_tp` FOREIGN KEY (`tp_id`) REFERENCES `tp` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='培养计划历史表';

DROP TABLE IF EXISTS `training_program`;
CREATE TABLE `training_program` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) NOT NULL COMMENT '培养计划名称',
  `major_id` bigint NOT NULL COMMENT '专业ID',
  `college_id` bigint NOT NULL COMMENT '学院ID',
  `year` int DEFAULT NULL COMMENT '年份',
  `description` text COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `fk_training_program_major_id` (`major_id`),
  KEY `fk_training_program_college_id` (`college_id`),
  CONSTRAINT `fk_training_program_college_id` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_training_program_major_id` FOREIGN KEY (`major_id`) REFERENCES `major` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='培养计划表';

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '主键',
  `username` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码',
  `college_id` bigint DEFAULT NULL COMMENT '所属学院ID（引用 college.id）',
  `dict_id` bigint DEFAULT NULL COMMENT '用户权限（关联字典表）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_idx` (`username`) USING BTREE,
  KEY `idx_user_college_id` (`college_id`),
  KEY `idx_user_dict_id` (`dict_id`),
  CONSTRAINT `fk_user_college` FOREIGN KEY (`college_id`) REFERENCES `college` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_user_sys_dict` FOREIGN KEY (`dict_id`) REFERENCES `sys_dict` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

INSERT INTO `college` (`id`, `college_code`, `college_name`, `create_time`, `update_time`, `del_flag`) VALUES
(1988456399996981248, '01', '计算机科学与通信工程学院', '2025-11-12 11:58:44', '2025-11-12 18:50:27', 0),
(1988456455047221248, '05', '马克思学院', '2025-11-12 11:58:57', '2025-11-19 16:24:26', 0);

INSERT INTO `course` (`id`, `course_code`, `dict_id`, `course_nature`, `course_name`, `college_id`, `major_id`, `total_credits`, `total_hours`, `total_weeks`, `hours_unit`, `hour_teach`, `hour_practice`, `hour_operation`, `hour_outside`, `hour_week`, `term`, `required_elective`, `course_choose_id`, `remark`, `create_time`, `update_time`, `del_flag`) VALUES
(1989198349058465792, 'JAVA', 1988933958606082048, 0, 'Java开发', 1988456399996981248, 1988535504914460672, 3.6, 0.0, 1.5, 1, 40, 16, NULL, 6, 3, 1, 1, NULL, '', '2025-11-14 13:06:59', '2025-11-14 21:18:48', 0);

INSERT INTO `major` (`id`, `major_code`, `major_name`, `college_id`, `course_num`, `category_id`, `create_time`, `update_time`, `del_flag`) VALUES
(1988533145819754496, '0001', '软件工程', 1988456399996981248, 99, 0, '2025-11-12 17:03:42', '2025-11-14 21:18:48', 0),
(1988535504914460672, '0002', '计算机科学与通信工程', 1988456455047221248, 104, 2, '2025-11-12 17:13:04', '2025-11-14 21:18:48', 0);

INSERT INTO `sys_dict` (`id`, `dict_type`, `dict_code`, `dict_name`, `sort_order`, `remark`, `create_time`, `update_time`, `del_flag`) VALUES
(1988933471567695872, 'course_type', 'GENERAL_EDUCATION', '通识教育', 1, '课程类型的字典，通识教育', '2025-11-13 19:34:27', '2025-11-13 19:34:27', 0),
(1988933958606082048, 'course_type', 'DISCIPLINARY_FOUNDATION', '学科专业基础', 1, '课程类型的字典，学科专业基础', '2025-11-13 19:36:23', '2025-11-13 19:36:23', 0),
(1988934119411503104, 'course_type', 'SPECIALIZED_COURSES', '专业课程', 1, '课程类型的字典，专业课程', '2025-11-13 19:37:01', '2025-11-13 19:37:01', 0),
(1988934279491309568, 'course_type', 'EXPERIMENTAL_PRACTICAL_SESSIONS', '实验实践环节', 1, '课程类型的字典，实验实践环节', '2025-11-13 19:37:40', '2025-11-13 19:37:40', 0),
(1988934379617734656, 'course_type', 'ROLE_ADVANCED_STUDIES', '进阶研学', 1, '课程类型的字典，进阶研学', '2025-11-13 19:38:03', '2025-11-18 19:42:50', 0),
(1988934680181559296, 'user_state', 'ROLE_ACADEMIC_AFFAIRS_STAFF', '教务处人员', 100, '用户身份类型的字典，教务处人员', '2025-11-13 19:39:15', '2025-11-19 15:11:53', 0),
(1988934779590758400, 'user_state', 'ROLE_DEPARTMENTAL_ACADEMIC_AFFAIRS', '系教务', 50, '用户身份类型的字典，系教务', '2025-11-13 19:39:39', '2025-11-19 15:11:53', 0),
(1988934871961915392, 'user_state', 'ROLE_DEPARTMENT_CHAIR', '系主任', 10, '用户身份类型的字典，系主任', '2025-11-13 19:40:01', '2025-11-19 15:11:53', 0);

INSERT INTO `user` (`id`, `username`, `password`, `college_id`, `dict_id`, `create_time`, `update_time`, `del_flag`) VALUES
(1991081015491952640, 'Somehow', '$2a$10$b27B.jsk7rh7IdPb7WRUyuu6QfRi4j4jKaVlYQDCeBhjJJIFKEC86', 1988456399996981248, 1988934680181559296, '2025-11-19 17:48:01', '2025-11-19 17:48:01', 0);



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;