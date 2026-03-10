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
    @Schema(description = "专业编号(可为空，如有需要再添加)", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    private String majorCode;

    /**
     * 学院Id
     */
    @Schema(description = "所属学院Id", example = "1988456399996981248", requiredMode = Schema.RequiredMode.REQUIRED)
    private String collegeId;

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", example = "软件工程", requiredMode = Schema.RequiredMode.REQUIRED)
    private String majorName;

    /**
     * 课程总数
     */
    @Schema(description = "课程总数(可为空，如有需要再添加)", example = "", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer courseNum;

    /**
     * 专业分类（0:工学 1:理学 2:文科）
     */
    @Schema(description = "专业分类（0:工学 1:理学 2:文科）", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer category;

}
