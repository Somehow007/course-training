package com.ujs.trainingprogram.tp.dto.req.trainingprogram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 培养计划课程更新请求体
 */
@Data
@Schema(description = "培养计划课程更新请求体")
public class TrainingProgramUpdateCourseReqDTO {

    /**
     * 培养计划详情id
     */
    @Schema(description = "培养计划详情id", example = "xx", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    /**
     * 修读专业id
     */
    @Schema(description = "修读专业id", example = "1988533145819754496")
    private String majorId;

    /**
     * 总学分
     */
    @Schema(description = "总学分", example = "3.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float totalCredits;

    /**
     * 总学时（小时）
     */
    @Schema(description = "总学时（小时）", example = "48")
    private Float totalHours;

    /**
     * 总学时（周）
     */
    @Schema(description = "总学时（周）", example = "")
    private Float totalWeeks;

    /**
     * 当前使用的学时单位
     */
    @Schema(description = "当前使用的学时单位", example = "")
    private Integer hoursUnit;

    /**
     * 授课学时
     */
    @Schema(description = "授课学时", example = "40")
    private Integer hourTeach;

    /**
     * 实验学时
     */
    @Schema(description = "实验学时", example = "16")
    private Integer hourPractice;

    /**
     * 上机学时
     */
    @Schema(description = "上机学时", example = "32")
    private Integer hourOperation;

    /**
     * 实践学时
     */
    @Schema(description = "实践学时", example = "6")
    private Integer hourOutside;

    /**
     * 周学时
     */
    @Schema(description = "周学时", example = "")
    private Integer hourWeek;

    /**
     * 选修学分要求
     */
    @Schema(description = "选修学分要求", example = "")
    private Integer requiredElective;

    /**
     * 建议修读学期
     */
    @Schema(description = "建议修读学期", example = "")
    private Integer term;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "")
    private String remark;
}
