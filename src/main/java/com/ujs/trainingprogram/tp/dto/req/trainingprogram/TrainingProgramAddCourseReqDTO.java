package com.ujs.trainingprogram.tp.dto.req.trainingprogram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 为培养计划添加课程请求体
 */
@Data
@Schema(description = "为培养计划添加课程请求体")
public class TrainingProgramAddCourseReqDTO {

    /**
     * 培养计划 Id
     */
    @Schema(description = "培养计划 Id", example = "1988933958606082048", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainingProgramId;

    /**
     * 课程 Id
     */
    @Schema(description = "课程 Id", example = "1988933958606082048", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courseId;

    /**
     * 开课学院id
     */
    @Schema(description = "开课学院id", example = "1988456399996981248")
    private String collegeId;

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
