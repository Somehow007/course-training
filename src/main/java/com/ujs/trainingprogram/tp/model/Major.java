package com.ujs.trainingprogram.tp.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 专业POJO
 */
@Data
@TableName("major")
public class Major {
    @TableId("major_id")
    private String majorId;//专业编号
    private String collegeId;//学院编号，实际页面中需转化成学院名称
    private String majorName;//专业名
    private Integer courseNum;//课程总数
    private Integer majorType;//专业类别 0-工学 1-理学 2-文科
}
