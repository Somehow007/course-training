package com.ujs.trainingprogram.tp.dto.resp.courseexclusivity;

import lombok.Data;

/**
 * 课程分类情况返回实体
 */
@Data
public class CourseToExclusivityRespDTO {

    /**
     * 分组编码
     */
    private String groupCode;

    /**
     * 课程 Id
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

}
