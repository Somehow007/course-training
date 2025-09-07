package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.model.College;

import java.util.List;

public interface CollegeService extends IService<College> {
    String getMaxCollegeId();

    List<College> getCollegeLikeByName(String collegeName);

    void countAll();

    ResultData selectWithWrapper(long cur, long size, QueryWrapper<College> wrapper);

    void modifyCourseNum(String collegeId, int num);

    List<College> getCollegeNameAndId();

    College getCollegeByName(String collegeName);

//    List<College> getCollegeNameAndCourseNum();
}
