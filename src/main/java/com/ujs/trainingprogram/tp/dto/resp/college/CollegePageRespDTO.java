package com.ujs.trainingprogram.tp.dto.resp.college;

import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import lombok.Data;

import java.util.List;

/**
 * 学院分页查询返回参数
 */
@Data
public class CollegePageRespDTO {

    /**
     * ID 学院编号
     */
    private String collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 学院中的专业
     */
    private List<MajorDO> majors;

    /**
     * 总课程数
     */
    private Integer courseNum;
}
