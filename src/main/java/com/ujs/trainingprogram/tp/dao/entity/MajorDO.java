package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class MajorDO {

    /**
     * 专业编号
     */
    @TableId("major_id")
    private String majorId;

    /**
     * 学院编号，实际页面中需转化成学院名称
     */
    private String collegeId;

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
