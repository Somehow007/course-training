package com.ujs.trainingprogram.tp.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 学院POJO
 */
@Data
@TableName("college")
public class College {
    @TableId(value = "college_id")
    private String collegeId;//学院编号
    private String collegeName;//学院名称
    private Integer courseNum;//总课程数
}
