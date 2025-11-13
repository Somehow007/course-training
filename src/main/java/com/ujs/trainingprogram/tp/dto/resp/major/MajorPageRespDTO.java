package com.ujs.trainingprogram.tp.dto.resp.major;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 分页查询专业返回实体
 */
@Data
public class MajorPageRespDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 专业编号
     */
    private String majorCode;

    /**
     * 学院名称
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
     * 专业分类（0:工学 1:理学 2:文科）
     */
    private Integer categoryId;

}
