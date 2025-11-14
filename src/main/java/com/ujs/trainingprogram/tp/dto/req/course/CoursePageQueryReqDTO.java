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
    private Long id;

    /**
     * 课程类别关联id
     */
    @Schema(description = "课程类别", example = "")
    private Long dictId;

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

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", example = "软件工程")
    private String majorName;

    /**
     * 修读专业id
     */
    @Schema(description = "修读专业id", example = "")
    private Long majorId;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "计算机科学与通信工程学院")
    private String collegeName;

    /**
     * 开课学院id
     */
    @Schema(description = "开课学院id", example = "")
    private Long collegeId;

}
