package com.ujs.trainingprogram.tp.dto.req.major;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 新建专业请求实体
 */
@Data
@Schema(description = "新建专业请求实体")
public class MajorSaveReqDTO {

    /**
     * 专业编号
     */
    @Schema(description = "专业编号", example = "0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String majorCode;

    /**
     * 学院编号
     */
    @Schema(description = "学院编号", example = "01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collegeCode;

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", example = "软件工程", requiredMode = Schema.RequiredMode.REQUIRED)
    private String majorName;

    /**
     * 课程总数
     */
    @Schema(description = "课程总数", example = "100", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer courseNum;

    /**
     * 专业分类（0:工学 1:理学 2:文科）
     */
    @Schema(description = "专业分类（0:工学 1:理学 2:文科）", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer categoryId;

}
