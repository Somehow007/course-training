package com.ujs.trainingprogram.tp.dto.req.trainingprogram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 从 Excel 中为培养计划添加课程请求体
 */
@Data
@Builder
public class TrainingProgramAddCourseFromExcelReqDTO {

    /**
     * 培养计划 Id
     */
    private String trainingProgramId;

    /**
     * 课程 Id
     */
    private Long courseId;

    /**
     * 课程性质（0:必修 1:选修）
     */
    private Integer courseNature;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 开课学院id
     */
    private Long collegeId;

    /**
     * 修读专业id
     */
    private Long majorId;

    /**
     * 总学分
     */
    private Float totalCredits;

    /**
     * 总学时
     */
    private String totalHours;

    /**
     * 授课学时
     */
    private Integer hourTeach;

    /**
     * 实验学时
     */
    private Integer hourPractice;

    /**
     * 上机学时
     */
    private Integer hourOperation;

    /**
     * 实践学时
     */
    private Integer hourOutside;

    /**
     * 周学时
     */
    private Integer hourWeek;

    /**
     * 选修学分要求
     */
    private Integer requiredElective;

    /**
     * 建议修读学期
     */
    private Integer term;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本号
     */
    private Integer version;
}
