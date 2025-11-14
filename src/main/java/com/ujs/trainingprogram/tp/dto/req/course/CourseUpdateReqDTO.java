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
    @Schema(description = "课程id")
    private String courseId;

    /**
     * 课程类别关联id
     */
    @Schema(description = "课程类别关联id", example = "1988933958606082048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dictId;

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

    /**
     * 开课学院id
     */
    @Schema(description = "开课学院id", example = "1988456399996981248")
    private Long collegeId;

    /**
     * 修读专业id
     */
    @Schema(description = "修读专业id", example = "1988533145819754496")
    private Long majorId;

    /**
     * 总学分
     */
    @Schema(description = "总学分", example = "3.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float totalCredits;

    /**
     * 总学时（单位：小时）二选一必填
     */
    @Schema(description = "总学时（单位：小时）", example = "48")
    private Float totalHours;

    /**
     * 总学时（单位：周）
     */
    @Schema(description = "总学时（单位：周）", example = "")
    private Float totalWeeks;

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
    private Integer hourOption;

    /**
     * 实践学时
     */
    @Schema(description = "实践学时", example = "6")
    private Integer hourOutside;

    /**
     * 周学时
     */
    @Schema(description = "周学时", example = "3")
    private Integer hourWeek;

    /**
     * 选修学分要求
     */
    @Schema(description = "选修学分要求", example = "1")
    private Integer requiredElective;

    /**
     * 建议修读学期
     */
    @Schema(description = "建议修读学期", example = "1")
    private Integer term;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "")
    private String remark;
}
