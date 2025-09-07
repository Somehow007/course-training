package com.ujs.trainingprogram.tp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.trainingprogram.tp.model.Major;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.toolkit.Constants;

public interface MajorMapper extends BaseMapper<Major> {
    @Select("SELECT MAX(major_id) FROM major #{ew.customSqlSegment}")
    String getMaxMajorId(@Param(Constants.WRAPPER)QueryWrapper<Major> wrapper);
}
