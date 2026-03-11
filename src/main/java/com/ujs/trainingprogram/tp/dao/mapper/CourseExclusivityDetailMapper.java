package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.trainingprogram.tp.dao.entity.CourseExclusivityDetailDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程分组详情持久层
 */
public interface CourseExclusivityDetailMapper extends BaseMapper<CourseExclusivityDetailDO> {

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<CourseExclusivityDetailDO> list);
}
