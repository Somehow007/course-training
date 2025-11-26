package com.ujs.trainingprogram.tp.dto.req.trainingprogram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建培养计划请求参数
 */
@Data
@Schema(description = "创建培养计划请求参数")
public class TrainingProgramCreateReqDTO {
    
    /**
     * 培养计划名称
     */
    @Schema(description = "培养计划名称", example = "软件工程培养计划")
    private String name;

    /**
     * 专业id
     */
    @Schema(description = "专业id", example = "")
    private String majorId;

    /**
     * 学院id
     */
    @Schema(description = "学院id", example = "")
    private String collegeId;

    /**
     * 年份
     */
    @Schema(description = "年份", example = "2024")
    private Integer year;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "软件工程培养计划")
    private String description;
}
