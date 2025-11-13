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
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 学院编号，创建时即不可更改
     */
    private String collegeCode;

    /**
     * 学院名称
     */
    private String collegeName;

}
