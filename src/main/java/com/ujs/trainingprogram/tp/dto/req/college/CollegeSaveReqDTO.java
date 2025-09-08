package com.ujs.trainingprogram.tp.dto.req.college;

import lombok.Data;

/**
 * 学院创建请求参数
 */
@Data
public class CollegeSaveReqDTO {

    /**
     * ID 学院编号
     */
    private String collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 总课程数
     */
    private Integer courseNum;
}
