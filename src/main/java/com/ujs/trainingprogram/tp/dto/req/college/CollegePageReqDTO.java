package com.ujs.trainingprogram.tp.dto.req.college;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import lombok.Data;

/**
 * 学院分页请求参数
 */
@Data
public class CollegePageReqDTO extends Page<CollegeDO> {

    /**
     * ID 学院编号
     */
    private String collegeId;

    /**
     * 学院名称
     */
    private String collegeName;
}
