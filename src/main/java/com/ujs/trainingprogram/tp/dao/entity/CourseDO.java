package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
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
public class CourseDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程类别关联id
     */
    private Long dictId;

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
}
