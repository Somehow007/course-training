package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;

import java.util.List;

public interface MajorService extends IService<MajorDO> {
    String getMaxMajorId(QueryWrapper<MajorDO> wrapper);
    List<MajorDO> getMajorLikeByName(String majorName);
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<MajorDO> wrapper);
    void countAll();
    void modifyCourseNum(String majorId, int num);
    List<MajorDO> getMajorByCollegeId(String collegeId);
}
