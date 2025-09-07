package com.ujs.trainingprogram.tp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.trainingprogram.tp.model.College;
import org.apache.ibatis.annotations.Select;

public interface CollegeMapper extends BaseMapper<College> {
    @Select("SELECT MAX(college_id) FROM college")
    String getMaxCollegeId();
}
