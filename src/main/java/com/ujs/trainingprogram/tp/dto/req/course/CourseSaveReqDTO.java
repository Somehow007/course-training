package com.ujs.trainingprogram.tp.dto.req.course;

import com.ujs.trainingprogram.tp.dao.entity.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseTypeEnum;
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
    private Integer courseCode;

    /**
     * 课程代码
     */
    @Schema(description = "课程代码")
    private String courseCode;

    /**
     * 课程类别
     */
    @Schema(description = "课程类别")
    private CourseTypeEnum courseType;

    /**
     * 课程性质
     */
    @Schema(description = "课程性质")
    private CourseCategoryEnum courseCategory;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 开课学院名称
     */
    @Schema(description = "开课学院名称")
    private String collegeName;

    /**
     * 修读专业名称
     */
    @Schema(description = "修读专业名称")
    private String majorName;

    /**
     * 总学分
     */
    @Schema(description = "总学分")
    private Float totalCredits;

    /**
     * 总学时
     */
    @Schema(description = "总学时")
    private Float totalHours;

    /**
     * 总学时_周
     */
    @Schema(description = "总学时_周")
    private Float totalWeeks;

    /**
     * 授课学时
     */
    @Schema(description = "授课学时")
    private Integer hourTeach;

    /**
     * 实验学时
     */
    @Schema(description = "实验学时")
    private Integer hourPractice;

    /**
     * 上机学时
     */
    @Schema(description = "上机学时")
    private Integer hourOption;

    /**
     * 实践学时
     */
    @Schema(description = "实践学时")
    private Integer hourOutside;

    /**
     * 周学时
     */
    @Schema(description = "周学时")
    private Integer hourWeek;

    /**
     * 选修学分要求
     */
    @Schema(description = "选修学分要求")
    private Byte requiredElective;

    /**
     * 建议修读学期
     */
    @Schema(description = "建议修读学期")
    private Integer term;
}
