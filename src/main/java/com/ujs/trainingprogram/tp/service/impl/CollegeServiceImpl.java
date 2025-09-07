package com.ujs.trainingprogram.tp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.model.College;
import com.ujs.trainingprogram.tp.model.Major;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {
    @Autowired
    @Lazy
    private MajorService majorService;
    @Autowired
    @Lazy
    private CourseService courseService;

    @Override
    public String getMaxCollegeId() {
        return getBaseMapper().getMaxCollegeId();
    }

    /**
     * 通过学院名称模糊查找
     * @param collegeName
     * @return
     */
    @Override
    public List<College> getCollegeLikeByName(String collegeName) {
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.like("college_name", collegeName);
        return getBaseMapper().selectList(wrapper);
    }



    /**
     * 获取学院的总课程数
     */
    @Override
    public void countAll() {
        List<College> list = getBaseMapper().selectList(new QueryWrapper<>());
        list.forEach(college ->
                college.setCourseNum(courseService.selectCountWithCollege(college.getCollegeId())));
        saveOrUpdateBatch(list);
    }

    /**
     * 自定义查询
     * @param cur
     * @param size
     * @param wrapper
     * @return
     */
    @Override
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<College> wrapper) {
        Page<College> page = new Page<>(cur, size);
        Page<College> collegePage = getBaseMapper().selectPage(page, wrapper);
        ResultData resultData = new ResultData();
        List<College> records = collegePage.getRecords();
        records.forEach(college -> {
            List<Major> majors = majorService.getMajorByCollegeId(college.getCollegeId());
            college.setCourseNum(courseService.selectCountWithCollege(college.getCollegeId()));
        });
        resultData.setData(records);
        resultData.setCur(collegePage.getCurrent());
        resultData.setSize(collegePage.getSize());
        resultData.setTotal(collegePage.getTotal());

        return resultData;
    }

    /**
     * 修改学院课程数量
     * @param collegeId
     * @param num
     */
    @Override
    public void modifyCourseNum(String collegeId, int num) {
        College college = getById(collegeId);
        college.setCourseNum(college.getCourseNum() + num);
        saveOrUpdate(college);
    }


    @Override
    public List<College> getCollegeNameAndId() {
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.select("college_id", "college_name");
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public College getCollegeByName(String collegeName) {
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        return getBaseMapper().selectOne(wrapper);
    }

//    @Override
//    public List<College> getCollegeNameAndCourseNum() {
//        QueryWrapper<College> wrapper = new QueryWrapper<>();
//        wrapper.select("college_name", "course_num");
//        return getBaseMapper().selectList(wrapper);
//    }
}
