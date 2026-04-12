package com.ujs.trainingprogram.tp.dto.req.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "版本对比请求DTO")
public class VersionCompareReqDTO {

    @Schema(description = "源版本ID")
    private String sourceVersionId;

    @Schema(description = "目标版本ID")
    private String targetVersionId;
}
