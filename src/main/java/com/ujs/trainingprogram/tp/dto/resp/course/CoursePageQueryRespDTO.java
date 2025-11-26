package com.ujs.trainingprogram.tp.dto.resp.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ujs.trainingprogram.tp.common.enums.CourseTypeEnum;
import lombok.Data;

/**
 * 分页查询课程返回实体
 */
@Data
public class CoursePageQueryRespDTO {

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 课程类别
     */
    private String courseType;

    /**
     * 课程性质
     */
    private Integer courseNature;

    /**
     * 课程性质（字符串形式）
     */
    @JsonProperty("courseNatureDesc")
    public String getCourseNatureDesc() {
        return CourseTypeEnum.getDictName(this.courseNature);
    }

    /**
     * 课程名称
     */
    private String courseName;
}
