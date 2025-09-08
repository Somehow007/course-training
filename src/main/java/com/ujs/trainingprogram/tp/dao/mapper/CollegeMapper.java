package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import org.apache.ibatis.annotations.Select;

/**
 * 学院持久层
 */
public interface CollegeMapper extends BaseMapper<CollegeDO> {
    @Select("SELECT MAX(college_id) FROM college")
    String getMaxCollegeId();

    /**
     * 分页统计学院
     */
    IPage<CollegeDO> pageResult(CollegePageReqDTO requestParam);

}
