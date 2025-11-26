package com.ujs.trainingprogram.tp.dto.req.course;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询课程请求实体
 */
@Data
@Schema(description = "分页查询课程请求实体")
public class CoursePageQueryReqDTO extends Page {

    /**
     * 课程id
     */
    @Schema(description = "课程id", example = "1989198349058465792")
    private String id;

    /**
     * 课程类别关联id
     */
    @Schema(description = "课程类别", example = "")
    private String dictId;

    /**
     * 课程类别
     */
    @Schema(description = "课程类别", example = "通识教育")
    private String dictName;

    /**
     * 课程性质（0:必修 1:选修）
     */
    @Schema(description = "课程性质（0:必修 1:选修）", example = "0")
    private Integer courseNature;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "Java开发")
    private String courseName;

}
