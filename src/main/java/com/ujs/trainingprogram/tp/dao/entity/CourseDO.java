package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseTypeEnum;
import lombok.*;

import java.math.BigDecimal;

/**
 * 课程实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course")
public class CourseDO {

    /**
     * 课程编号
     */
    @TableId(value = "course_id", type = IdType.AUTO)
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
     * 总学时，处理单位为周的字段，获取其数字
     */
    @TableField(exist = false)
    @Getter(AccessLevel.NONE)
    private BigDecimal totalWeeksNum;

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

    // 处理单位为周的字段，获取其数字
    public BigDecimal getTotalWeeksNum() {
        if (totalWeeks == null) {
            return null;
        }
        return new BigDecimal(totalWeeks.replace("周", ""));
    }
}
