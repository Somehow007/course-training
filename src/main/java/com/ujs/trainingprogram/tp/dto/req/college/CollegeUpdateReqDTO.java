package com.ujs.trainingprogram.tp.dto.req.college;

import lombok.Data;

/**
 * 学院修改请求对象
 */
@Data
public class CollegeUpdateReqDTO {

    /**
     * ID 原学院编号
     */
    private String originCollegeId;

    /**
     * ID 新学院编号
     */
    private String newCollegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 总课程数
     */
    private Integer courseNum;
}
