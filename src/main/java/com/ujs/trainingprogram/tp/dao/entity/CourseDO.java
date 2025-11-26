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

}
