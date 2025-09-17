package com.ujs.trainingprogram.tp.dto;

import lombok.Data;

/**
 * 分页查询学院信息，扁平化 DTO
 */
@Data
public class CollegePageMajorDTO {

    /**
     * 学院编号
     */
    private String collegeCode;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 专业编号
     */
    private String majorCode;

    /**
     * 专业名
     */
    private String majorName;

    /**
     * 当前专业课程总数
     */
    private Integer courseNum;

    /**
     * 专业类别 0-工学 1-理学 2-文科
     */
    private Integer majorType;
}
