package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dto.req.course.CoursePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.course.CoursePageRespDTO;

import java.util.List;

public interface CourseService extends IService<CourseDO> {

    /**
     * 分页查询课程信息
     *
     * @param requestParam 分页查询课程信息请求内容
     * @return  课程信息分页返回结果
     */
    IPage<CoursePageRespDTO> pageCourse(CoursePageReqDTO requestParam);

    /**
     * 添加新的课程
     *
     * @param requestParam  添加新课程的请求参数实体
     */
    void createCourse(CourseSaveReqDTO requestParam);

    List<CourseDO> selectCourseByCodeAndYear(String code, String year);
    Integer selectCountWithCollege(String collegeId);//查询学院课程数量
    Integer selectCountWithMajor(String majorId);//查询专业课程数量

    ResultData selectWithWrapper(long cur, long size, QueryWrapper<CourseDO> wrapper);
    ResultData selectSimple(String code, String year, String state, String collegeId);
    ResultData selectWithCourseId(long cur, long size, Integer courseId);
    List<CourseDO> selectWithYear(String year);
}
