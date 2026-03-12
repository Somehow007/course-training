package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.CourseExclusivityDO;
import com.ujs.trainingprogram.tp.dto.CollegePageMajorDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.courseexclusivity.CourseToExclusivityRespDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 课程分组持久层
 */
public interface CourseExclusivityMapper extends BaseMapper<CourseExclusivityDO> {

    /**
     * 根据培养计划 Id 查询课程及其分组情况
     *
     * @param tpId    培养计划 Id
     * @return      课程及其分组情况
     */
    List<CourseToExclusivityRespDTO> selectCourseToExclusivity(@Param("tpId") String tpId);
}
