package com.ujs.trainingprogram.tp.dto.resp.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ujs.trainingprogram.tp.common.enums.CourseTypeEnum;
import lombok.Data;

/**
 * 分页查询课程返回实体
 */
@Data
public class CoursePageQueryRespDTO {

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 课程类别
     */
    private String courseType;

    /**
     * 课程性质
     */
    private Integer courseNature;

    /**
     * 课程性质（字符串形式）
     */
    @JsonProperty("courseNatureDesc")
    public String getCourseNatureDesc() {
        return CourseTypeEnum.getDictName(this.courseNature);
    }

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 开课学院名称
     */
    private String collegeName;

    /**
     * 修读专业名称
     */
    private String majorName;

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
}
