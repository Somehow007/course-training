package com.ujs.trainingprogram.tp.dto.req.college;

import lombok.Builder;
import lombok.Data;

/**
 * 分页查询学院获取的 专业 DTO
 */
@Data
@Builder
public class CollegeMajorPageRespDTO {

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
     * 专业分类（0:工学 1:理学 2:文科）
     */
    private Integer categoryId;

}
