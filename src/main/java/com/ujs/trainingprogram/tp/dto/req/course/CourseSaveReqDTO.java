package com.ujs.trainingprogram.tp.dto.req.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存课程请求实体
 */
@Data
@Schema(description = "保存课程请求实体")
public class CourseSaveReqDTO {

    /**
     * 课程编号
     */
    @Schema(description = "课程编号")
    private String courseCode;

    /**
     * 课程类别关联id
     */
    @Schema(description = "课程类别关联id", example = "1988933958606082048", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictId;

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
