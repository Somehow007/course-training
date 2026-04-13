package com.ujs.trainingprogram.tp.dto.resp.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ujs.trainingprogram.tp.common.enums.CourseNatureEnum;
import lombok.Data;

@Data
public class CoursePageQueryRespDTO {

    private Long courseId;

    private String courseType;

    private Integer courseNature;

    @JsonProperty("courseNatureDesc")
    public String getCourseNatureDesc() {
        return CourseNatureEnum.findValueByType(this.courseNature);
    }

    private String courseName;

    private String collegeName;

    private Long collegeId;

    private Integer delFlag;
}
