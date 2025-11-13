package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.major.MajorPageRespDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.toolkit.Constants;

/**
 * 专业管理持久层
 */
public interface MajorMapper extends BaseMapper<MajorDO> {
    @Select("SELECT MAX(major_id) FROM major #{ew.customSqlSegment}")
    String getMaxMajorId(@Param(Constants.WRAPPER)QueryWrapper<MajorDO> wrapper);

    IPage<MajorPageRespDTO> pageMajorResults(Page<?> page, @Param("requestParam") MajorPageReqDTO requestParam);
}
