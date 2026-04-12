package com.ujs.trainingprogram.tp.dto.resp.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "版本列表响应DTO")
public class VersionListRespDTO {

    @Schema(description = "版本ID")
    private String id;

    @Schema(description = "培养方案ID")
    private String trainingProgramId;

    @Schema(description = "培养方案名称")
    private String trainingProgramName;

    @Schema(description = "版本号")
    private Integer versionNumber;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "版本状态")
    private Integer versionStatus;

    @Schema(description = "版本状态描述")
    private String versionStatusDesc;

    @Schema(description = "变更说明")
    private String changeDescription;

    @Schema(description = "创建人ID")
    private String creatorId;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "发布人姓名")
    private String publishUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
