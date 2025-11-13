package com.ujs.trainingprogram.tp.dto.req.college;

import lombok.Data;

/**
 * 学院修改请求对象
 */
@Data
public class CollegeUpdateReqDTO {

    /**
     * 学院编号
     */
    private String collegeCode;

    /**
     * 学院名称
     */
    private String collegeName;

}
