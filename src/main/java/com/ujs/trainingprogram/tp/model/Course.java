package com.ujs.trainingprogram.tp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.model.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.model.enums.CourseTypeEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 课程POJO
 */
@Data
@TableName("course")
public class Course {
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;//课程编号
    private String courseCode;//课程代码
    private CourseTypeEnum courseType;//课程类别
    private CourseCategoryEnum courseCategory;//课程性质
    private String courseName;//课程名称
    private String collegeId;//开课学院编号
    private String majorId;//修读专业编号
    private Float totalCredits;//总学分
    private Float totalHours;//总学时
    private String totalWeeks;//总学时，单位周
    @TableField(exist = false)
    @Getter(AccessLevel.NONE)
    private BigDecimal totalWeeksNum;//总学时，处理单位为周的字段，获取其数字
    private Integer hourTeach;//授课学时
    private Integer hourPractice;//实验学时
    private Integer hourOperation;//上机学时
    private Integer hourOutside;//实践学时
    private Integer hourWeek;//周学时
    private byte requiredElective;//选修学分要求
    private Integer term;//建议修读学期
    private String remark;//备注

    // 处理单位为周的字段，获取其数字
    public BigDecimal getTotalWeeksNum() {
        if (totalWeeks == null) {
            return null;
        }
        return new BigDecimal(totalWeeks.replace("周", ""));
    }
}
