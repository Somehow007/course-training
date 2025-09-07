package com.ujs.trainingprogram.tp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.model.Major;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {
    @Autowired
    @Lazy
    private CourseService courseService;
    @Autowired
    @Lazy
    private CollegeService collegeService;

    @Override
    public String getMaxMajorId(QueryWrapper<Major> wrapper) {
        return getBaseMapper().getMaxMajorId(wrapper);
    }

    @Override
    public List<Major> getMajorLikeByName(String majorName) {
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        wrapper.like("major_name", majorName);
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public List<Major> getMajorByCollegeId(String collegeId) {
        QueryWrapper<Major> wrapper = new QueryWrapper<>();
        wrapper.eq("college_id", collegeId);
        return getBaseMapper().selectList(wrapper);
    }

    /**
     * 自定义条件查询
     * @param cur
     * @param size
     * @param wrapper
     * @return
     */
    @Override
    public ResultData selectWithWrapper(long cur, long size, QueryWrapper<Major> wrapper) {
        Page<Major> page = new Page<>(cur, size);
        Page<Major> majorPage = getBaseMapper().selectPage(page, wrapper);
        ResultData resultData = new ResultData();
        List<Major> records = majorPage.getRecords();
        records.forEach(major ->
                major.setCollegeId(collegeService.getById(major.getCollegeId()).getCollegeName()));
        resultData.setData(records);
        resultData.setCur(majorPage.getCurrent());
        resultData.setSize(majorPage.getSize());
        resultData.setTotal(majorPage.getSize());

        return resultData;
    }

    /**
     * 重新计算专业课程总数
     */
    @Override
    public void countAll() {
        List<Major> list = getBaseMapper().selectList(new QueryWrapper<>());
        list.forEach(major -> major.setCourseNum(courseService.selectCountWithMajor(major.getMajorId())));
        saveOrUpdateBatch(list);
    }

    /**
     * 修改专业课程数量
     * @param majorId
     * @param num
     */
    @Override
    public void modifyCourseNum(String majorId, int num) {
        Major major = getById(majorId);
        major.setCourseNum(major.getCourseNum() + num);
        saveOrUpdate(major);
    }
}
