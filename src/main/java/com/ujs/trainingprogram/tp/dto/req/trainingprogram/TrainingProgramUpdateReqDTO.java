package com.ujs.trainingprogram.tp.dto.req.trainingprogram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改培养计划请求体
 */
@Data
@Schema(description = "修改培养计划请求体")
public class TrainingProgramUpdateReqDTO {

    /**
     * 培养计划id
     */
    @Schema(description = "培养计划id", example = "1988933958606082048")
    private String id;

    /**
     * 培养计划名称
     */
    @Schema(description = "培养计划名称", example = "软件工程培养计划")
    private String name;

    /**
     * 年份
     */
    @Schema(description = "年份", example = "2024")
    private String year;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "软件工程培养计划")
    private String description;
}
