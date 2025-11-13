package com.ujs.trainingprogram.tp.dto.resp.course;

import com.ujs.trainingprogram.tp.dao.entity.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseTypeEnum;

/**
 * 分页查询课程返回实体
 */
public class CoursePageRespDTO {

    /**
     * 课程编号
     */
    private Integer courseId;

    /**
     * 课程代码
     */
    private String courseCode;

    /**
     * 课程类别
     */
    private CourseTypeEnum courseType;

    /**
     * todo：枚举类待优化
     * 课程性质
     */
    private CourseCategoryEnum courseCategory;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 开课学院编号
     */
    private String collegeId;

    /**
     * 修读专业编号
     */
    private String majorId;

    /**
     * 总学分
     */
    private Float totalCredits;

    /**
     * 总学时
     */
    private Float totalHours;

    /**
     * 总学时，单位周
     */
    private String totalWeeks;


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
    private byte requiredElective;

    /**
     * 建议修读学期
     */
    private Integer term;

    /**
     * 备注
     */
    private String remark;

}
