package com.ujs.trainingprogram.tp.dto.req.college;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学院创建请求参数
 */
@Data
@Schema(description = "创建学院请求参数")
public class CollegeSaveReqDTO {

    /**
     * 学院编号
     */
    @Schema(description = "学院编号", example = "01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collegeCode;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "计算机科学与通信工程学院", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collegeName;

    /**
     * 总课程数
     */
    @Schema(description = "总课程数", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer courseNum;
}
