package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选修课程分组数据库详情实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "course_exclusivity_detail")
public class CourseExclusivityDetailDO extends BaseDO {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 选修课程分组 Id
     */
    private Long exclusivityId;

    /**
     * 课程 Id
     */
    private Long courseId;
}
