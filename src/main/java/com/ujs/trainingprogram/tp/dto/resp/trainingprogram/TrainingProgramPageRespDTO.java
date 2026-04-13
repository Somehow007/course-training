package com.ujs.trainingprogram.tp.dto.resp.trainingprogram;

import lombok.Data;

/**
 * 分页查询培养计划返回实体
 */
@Data
public class TrainingProgramPageRespDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 培养计划名称
     */
    private String name;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学院ID
     */
    private Long collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 描述
     */
    private String description;

    /**
     * 当前版本号
     */
    private Integer currentVersion;

    /**
     * 版本状态（0:草稿 1:已发布 2:已归档 3:已回滚）
     */
    private Integer versionStatus;
}
