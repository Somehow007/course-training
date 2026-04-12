package com.ujs.trainingprogram.tp.dto.resp.trainingprogram;

import lombok.Data;

/**
 * 查询某专业的培养计划返回实体
 */
@Data
public class TrainingProgramDetailSelectRespDTO {

    /**
     * 培养计划详情id
     */
    private Long id;

    /**
     * 培养计划名称
     */
    private String name;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程性质（0:必修 1:选修）
     */
    private Integer courseNature;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 开课学院名称
     */
    private String collegeName;

    /**
     * 开课学院id
     */
    private Long collegeId;

    /**
     * 修读专业名称
     */
    private String majorName;

    /**
     * 修读专业id
     */
    private Long majorId;

    /**
     * 总学分
     */
    private Float totalCredits;

    /**
     * 总学时（小时）
     */
    private Float totalHours;

    /**
     * 总学时（周）
     */
    private Float totalWeeks;

    /**
     * 当前使用的学时单位
     */
    private Integer hoursUnit;

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
