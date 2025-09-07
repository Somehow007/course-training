package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.model.Major;

import java.util.List;

public interface MajorService extends IService<Major> {
    String getMaxMajorId(QueryWrapper<Major> wrapper);
    List<Major> getMajorLikeByName(String majorName);
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<Major> wrapper);
    void countAll();
    void modifyCourseNum(String majorId, int num);
    List<Major> getMajorByCollegeId(String collegeId);
}
