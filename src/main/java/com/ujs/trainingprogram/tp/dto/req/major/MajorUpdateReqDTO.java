package com.ujs.trainingprogram.tp.dto.req.major;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 专业信息更新请求实体
 */
@Data
@Schema(description = "更新专业信息请求参数")
public class MajorUpdateReqDTO {


    /**
     * 专业Id
     */
    @Schema(description = "专业Id", example = "1988533145819754496")
    private String majorId;

    /**
     * 学院Id
     */
    @Schema(description = "学院Id", example = "1988456399996981248")
    private String collegeId;

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", example = "软件工程")
    private String majorName;

    /**
     * 专业分类（0:工学 1:理学 2:文科）
     */
    @Schema(description = "专业分类（0:工学 1:理学 2:文科）", example = "0")
    private Integer category;
}
