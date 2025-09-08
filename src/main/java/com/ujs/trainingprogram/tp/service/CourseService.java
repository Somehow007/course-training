package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;

import java.util.List;

public interface CourseService extends IService<CourseDO> {
    List<CourseDO> selectCourseByCodeAndYear(String code, String year);
    Integer selectCountWithCollege(String collegeId);//查询学院课程数量
    Integer selectCountWithMajor(String majorId);//查询专业课程数量

    ResultData selectWithWrapper(long cur, long size, QueryWrapper<CourseDO> wrapper);
    ResultData selectSimple(String code, String year, String state, String collegeId);
    ResultData selectWithCourseId(long cur, long size, Integer courseId);
    List<CourseDO> selectWithYear(String year);
}
