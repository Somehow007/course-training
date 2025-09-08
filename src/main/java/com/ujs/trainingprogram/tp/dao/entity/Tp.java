package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 培养计划联合表
 */
@Data
@TableName("tp")
public class Tp {
    @TableId("tp_id")
    private Integer tpId;//联合表唯一id
    private String majorId;//专业
    private String collegeId;//学院
    private Integer year;//年份
    private LocalDateTime gmtUpdated;//上传时间
}
