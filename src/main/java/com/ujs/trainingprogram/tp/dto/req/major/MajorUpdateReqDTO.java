package com.ujs.trainingprogram.tp.dto.req.major;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 专业信息更新请求实体
 */
@Data
public class MajorUpdateReqDTO {

    /**
     * 专业编号
     */
    @Schema(name = "专业编号", example = "0001")
    private String majorId;

    /**
     * 学院编号
     */
    @Schema(name = "学院编号")
    private String collegeId;

    /**
     * 专业名称
     */
    @Schema(name = "专业名称", example = "软件工程")
    private String majorName;

    /**
     * 课程总数
     */
    @Schema(name = "课程总数", example = "100")
    private Integer courseNum;

    /**
     * 专业类别 0-工学 1-理学 2-文科
     */
    @Schema(name = "专业类别 0-工学 1-理学 2-文科", example = "0")
    private Integer majorType;
}
