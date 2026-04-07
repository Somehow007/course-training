package com.ujs.trainingprogram.tp.dto.req.trainingprogram;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询培养计划请求实体
 */
@Data
@Schema(description = "分页查询培养计划请求实体")
public class TrainingProgramPageReqDTO extends Page {

    /**
     * 专业ID
     */
    @Schema(description = "专业ID")
    private String majorId;

    /**
     * 学院ID
     */
    @Schema(description = "学院ID")
    private String collegeId;

    /**
     * 年份
     */
    @Schema(description = "年份")
    private Integer year;
}
