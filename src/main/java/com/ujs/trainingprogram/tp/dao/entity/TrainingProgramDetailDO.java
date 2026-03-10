package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 培养计划详情表
 */
@Data
@Builder
@TableName(value = "training_program_detail")
public class TrainingProgramDetailDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 培养计划 Id
     */
    private Long trainingProgramId;

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
