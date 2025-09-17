package com.ujs.trainingprogram.tp.dto.req.college;

import lombok.Data;

/**
 * 学院修改请求对象
 */
@Data
public class CollegeUpdateReqDTO {

    /**
     * 原学院编号
     */
    private String originCollegeCode;

    /**
     * 新学院编号
     */
    private String newCollegeCode;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 总课程数
     */
    private Integer courseNum;
}
