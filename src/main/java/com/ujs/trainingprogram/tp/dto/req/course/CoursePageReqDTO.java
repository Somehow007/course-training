package com.ujs.trainingprogram.tp.dto.req.course;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 分页查询课程请求实体
 */
@Data
@Schema(description = "分页查询课程请求实体")
public class CoursePageReqDTO extends Page {

    /**
     * 课程编号
     */
    @Schema(description = "课程编号", example = "001")
    private Integer courseId;

    /**
     * 课程代码
     */
    @Schema(description = "课程代码", example = "001")
    private String courseCode;

    /**
     * 课程类别
     */
    @Schema(description = "课程类别", example = "001")
    private CourseTypeEnum courseType;

    /**
     * todo：枚举类待优化
     * 课程性质
     */
    @Schema(description = "课程性质", example = "001")
    private CourseCategoryEnum courseCategory;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "001")
    private String courseName;

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", example = "001")
    private String majorName;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "001")
    private String collegeName;

    /**
     * 学年
     */
    @Schema(description = "学年", example = "001")
    private Integer year;


}
