package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("version_change_log")
public class VersionChangeLogDO {

    @TableId(value = "id")
    private Long id;

    private Long versionId;

    private Integer changeType;

    private String changeTarget;

    private Long targetId;

    private String oldValue;

    private String newValue;

    private String changeDescription;

    @TableField("create_time")
    private Date createTime;
}
