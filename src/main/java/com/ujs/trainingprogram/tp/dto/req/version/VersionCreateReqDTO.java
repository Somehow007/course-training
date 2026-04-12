package com.ujs.trainingprogram.tp.dto.req.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "版本创建请求DTO")
public class VersionCreateReqDTO {

    @Schema(description = "培养方案ID")
    private String trainingProgramId;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "变更说明")
    private String changeDescription;
}
