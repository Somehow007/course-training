package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ujs.trainingprogram.tp.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("version_history")
public class VersionHistoryDO extends BaseDO {

    @TableId(value = "id")
    private Long id;

    private Long trainingProgramId;

    private Integer versionNumber;

    private String versionName;

    private Integer versionStatus;

    private String changeDescription;

    private String snapshotData;

    private Long creatorId;

    private String creatorName;

    private LocalDateTime publishTime;

    private Long publishUserId;

    private String publishUserName;
}
