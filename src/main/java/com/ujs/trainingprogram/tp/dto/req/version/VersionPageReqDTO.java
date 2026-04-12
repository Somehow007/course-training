package com.ujs.trainingprogram.tp.dto.req.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "版本分页查询请求DTO")
public class VersionPageReqDTO {

    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "培养方案ID")
    private String trainingProgramId;

    @Schema(description = "版本状态")
    private Integer versionStatus;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "创建人姓名")
    private String creatorName;
}
