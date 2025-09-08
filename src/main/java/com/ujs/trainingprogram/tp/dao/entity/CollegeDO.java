package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学院实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("college")
public class CollegeDO extends BaseDO {

    /**
     * ID 学院编号
     */
    @TableId(value = "college_id")
    private String collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 总课程数
     */
    private Integer courseNum;
}
