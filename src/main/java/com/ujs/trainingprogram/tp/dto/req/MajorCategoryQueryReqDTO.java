package com.ujs.trainingprogram.tp.dto.req;

import lombok.Data;

/**
 * 查询专业分类配置请求实体
 */
@Data
public class MajorCategoryQueryReqDTO {

    /**
     * 学科门类，如“工学”
     */
    private String disciplineCategory;

    /**
     * 专业类别，如“计算机类”
     */
    private String professionalCategory;
}
