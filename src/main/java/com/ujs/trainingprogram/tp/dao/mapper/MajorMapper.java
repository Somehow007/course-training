package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.toolkit.Constants;

public interface MajorMapper extends BaseMapper<MajorDO> {
    @Select("SELECT MAX(major_id) FROM major #{ew.customSqlSegment}")
    String getMaxMajorId(@Param(Constants.WRAPPER)QueryWrapper<MajorDO> wrapper);
}
