package com.ujs.trainingprogram.tp.dto.req.college;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学院修改请求对象
 */
@Data
@Schema(description = "学院修改请求对象")
public class CollegeUpdateReqDTO {

    /**
     * 学院Id
     */
    @Schema(description = "学院Id", example = "1988456399996981248")
    private String collegeId;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "计算机科学与通信工程学院")
    private String collegeName;

}
