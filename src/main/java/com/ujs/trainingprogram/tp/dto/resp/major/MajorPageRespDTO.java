package com.ujs.trainingprogram.tp.dto.resp.major;

import lombok.Data;

/**
 * 分页查询专业返回实体
 */
@Data
public class MajorPageRespDTO {

    /**
     * 专业编号
     */
    private String id;

    /**
     * 学院
     */
    private String collegeName;

    /**
     * 专业名
     */
    private String majorName;

    /**
     * 课程总数
     */
    private Integer courseNum;

    /**
     * 专业类别 0-工学 1-理学 2-文科
     */
    private Integer majorType;
}
