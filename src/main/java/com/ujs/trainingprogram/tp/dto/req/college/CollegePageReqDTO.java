package com.ujs.trainingprogram.tp.dto.req.college;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查找学院请求参数实体
 */
@Data
@Schema(description = "分页查找学院请求参数实体")
public class CollegePageReqDTO extends Page {

    /**
     * 学院编号
     */
    @Schema(description = "学院编号", example = "todo，学院编号样式")
    private String collegeCode;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "计算机科学与通信工程学院")
    private String collegeName;
}
