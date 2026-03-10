package com.ujs.trainingprogram.tp.dto.req.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程更新请求实体
 */
@Data
@Schema(description = "课程更新请求实体")
public class CourseUpdateReqDTO {

    /**
     * 课程id
     */
    @Schema(description = "课程id", example = "1992813288416546816", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    /**
     * 课程类别关联id
     */
    @Schema(description = "课程类别关联id", example = "1988933958606082048", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictId;

    /**
     * 开课学院Id
     */
    @Schema(description = "开课学院Id", example = "1988456399996981248", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collegeId;

    /**
     * 课程性质（0:必修 1:选修）
     */
    @Schema(description = "课程性质（0:必修 1:选修）", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer courseNature;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "Java开发", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courseName;

}
