package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选修课程分组数据库实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "course_exclusivity")
public class CourseExclusivityDO extends BaseDO {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 分组编码
     */
    private String groupCode;

    /**
     * 培养计划 Id
     */
    private Long trainingProgramId;

    /**
     * 要求选修学分
     */
    private Integer requiredCredits;

    /**
     * 版本号，和培养计划详情表的保持一致
     */
    private Integer version;

}
