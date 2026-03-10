package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 培养计划表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("training_program")
public class TrainingProgramDO extends BaseDO {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 培养计划名称
     */
    private String name;

    /**
     * 专业id
     */
    private Long majorId;

    /**
     * 学院id
     */
    private Long collegeId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 描述
     */
    private String description;
}
