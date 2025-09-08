package com.ujs.trainingprogram.tp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseDO> implements CourseService {
    @Autowired
    @Lazy
    private CollegeService collegeService;
    @Autowired
    @Lazy
    private MajorService majorService;

    @Override
    public List<CourseDO> selectCourseByCodeAndYear(String code, String year) {
        QueryWrapper<CourseDO> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        wrapper.eq("year", year);
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * 学院课程总数
     *
     * @param collegeId
     * @return
     */
    @Override
    public Integer selectCountWithCollege(String collegeId) {
        QueryWrapper<CourseDO> wrapper = new QueryWrapper<>();
        wrapper.eq("college_id", collegeId);
        return getBaseMapper().selectCount(wrapper).intValue();
    }

    /**
     * 专业课程总数
     *
     * @param majorId
     * @return
     */
    @Override
    public Integer selectCountWithMajor(String majorId) {
        QueryWrapper<CourseDO> wrapper = new QueryWrapper<>();
        wrapper.eq("major_id", majorId);
        return getBaseMapper().selectCount(wrapper).intValue();
    }

    @Override
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<CourseDO> wrapper) {
        Page<CourseDO> page = new Page<>(cur, size);
        Page<CourseDO> coursePage = getBaseMapper().selectPage(page, wrapper);
        ResultData resultData = new ResultData();
        List<CourseDO> records = coursePage.getRecords();
        records.forEach(course -> {
            course.setCollegeId(collegeService.getById(course.getCollegeId()).getCollegeName());
            course.setMajorId(majorService.getById(course.getMajorId()).getMajorName());
        });
        resultData.setData(records);
        resultData.setCur(coursePage.getCurrent());
        resultData.setSize(coursePage.getSize());
        resultData.setTotal(coursePage.getTotal());

        return resultData;
    }

    @Override
    public ResultData selectSimple(String code, String year, String state, String collegeId) {
        return null;
    }

    @Override
    public ResultData selectWithCourseId(long cur, long size, Integer courseId) {
        return null;
    }

    @Override
    public List<CourseDO> selectWithYear(String year) {
        return List.of();
    }
}