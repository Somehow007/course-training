package com.ujs.trainingprogram.tp.dto.resp;

import lombok.Data;

/**
 * 学院创建返回参数
 */
@Data
public class CollegeSaveRespDTO {

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
