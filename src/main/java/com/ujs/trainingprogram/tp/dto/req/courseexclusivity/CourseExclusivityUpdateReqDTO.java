package com.ujs.trainingprogram.tp.dto.req.courseexclusivity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新课程分组请求实体
 */
@Data
@Schema(description = "更新课程分组请求实体")
public class CourseExclusivityUpdateReqDTO {

    /**
     * 选修课程分组 Id
     */
    @Schema(description = "课程分组 Id", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    private String exclusivityId;

    /**
     * 培养计划 Id
     */
    @Schema(description = "培养计划 Id", example = "1993554514534068224", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainingProgramId;

    /**
     * 要求选修学分
     */
    @Schema(description = "要求选修学分", example = "2")
    private String requiredCredits;
}
