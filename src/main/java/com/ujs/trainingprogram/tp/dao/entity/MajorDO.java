package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专业实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("major")
public class MajorDO extends BaseDO {

    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 专业编号
     */
    private String majorCode;

    /**
     * 学院编号，实际页面中需转化成学院名称
     */
    private Long collegeId;

    /**
     * 专业名
     */
    private String majorName;

    /**
     * 课程总数
     */
    private Integer courseNum;

    /**
     * 专业类别 0-工学 1-理学 2-文科
     */
    private Integer majorType;
}
