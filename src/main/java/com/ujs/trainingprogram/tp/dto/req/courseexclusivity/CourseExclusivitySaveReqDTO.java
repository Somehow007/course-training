package com.ujs.trainingprogram.tp.dto.req.courseexclusivity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 保存课程分组请求实体
 */
@Data
@Builder
@Schema(description = "保存课程分组请求实体")
public class CourseExclusivitySaveReqDTO {

    /**
     * 培养计划 Id
     */
    @Schema(description = "培养计划 Id", example = "1993554514534068224", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long trainingProgramId;

    /**
     * 分组编码
     */
    @Schema(description = "分组编码", example = "EXC_GROUP_{tpId}_{version}_{startRow}_{endRow}", requiredMode = Schema.RequiredMode.REQUIRED)
    private String groupCode;

    /**
     * 要求选修学分
     */
    @Schema(description = "要求选修学分", example = "2")
    private Integer requiredCredits;

    /**
     * 版本号，和培养计划详情表的版本一致
     */
    @Schema(description = "版本号", example = "1")
    private Integer version;

}
