package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dto.req.course.CoursePageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.course.CoursePageQueryRespDTO;

/**
 * 课程实体数据库持久层
 */
public interface CourseMapper extends BaseMapper<CourseDO> {

    /**
     * 分页统计短链接
     */
    IPage<CoursePageQueryRespDTO> pageCourseResults(CoursePageQueryReqDTO requestParam);
}
