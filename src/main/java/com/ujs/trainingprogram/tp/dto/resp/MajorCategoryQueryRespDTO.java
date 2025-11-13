package com.ujs.trainingprogram.tp.dto.resp;

import lombok.Data;

/**
 * 查询专业分类配置返回实体
 */
@Data
public class MajorCategoryQueryRespDTO {

    /**
     * 学科门类，如“工学”
     */
    private String disciplineCategory;

    /**
     * 专业类别，如“计算机类”
     */
    private String professionalCategory;

    /**
     * 描述（可选）
     */
    private String description;

    /**
     * 排序值
     */
    private Integer sortOrder;
}
