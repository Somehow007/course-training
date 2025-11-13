package com.ujs.trainingprogram.tp.dto.resp.college;

import com.baomidou.mybatisplus.annotation.TableId;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.CollegePageMajorDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeMajorPageRespDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学院分页查询返回参数
 */
@Data
@Builder
public class CollegePageRespDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 学院中的专业
     */
    private List<CollegeMajorPageRespDTO> majors;

    /**
     * 总课程数
     */
    private Integer courseNum;


    /**
     * 专业数
     */
    private Integer majorNum;
}
