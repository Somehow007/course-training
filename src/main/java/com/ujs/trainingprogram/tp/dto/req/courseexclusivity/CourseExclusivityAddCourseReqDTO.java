package com.ujs.trainingprogram.tp.dto.req.courseexclusivity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 为课程分组添加信息请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "为课程分组添加信息请求体")
public class CourseExclusivityAddCourseReqDTO {

    /**
     * 选修课程分组 Id
     */
    @Schema(description = "课程分组 Id", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    private String exclusivityId;

    /**
     * 课程 Id
     */
    @Schema(description = "课程 Id", example = "1992813288416546816", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courseId;
}
