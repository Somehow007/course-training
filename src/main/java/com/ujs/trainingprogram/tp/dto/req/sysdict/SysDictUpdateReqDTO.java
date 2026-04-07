package com.ujs.trainingprogram.tp.dto.req.sysdict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新系统字典请求实体
 */
@Data
@Schema(description = "更新系统字典请求实体")
public class SysDictUpdateReqDTO {

    @Schema(description = "字典ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "字典类型", example = "course_type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    @Schema(description = "字典编码", example = "GENERAL_EDUCATION", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictCode;

    @Schema(description = "字典名称", example = "通识教育", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    @Schema(description = "排序号", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sortOrder;

    @Schema(description = "备注", example = "课程类型的字典，通识教育", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
