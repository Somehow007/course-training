package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dao.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.dto.req.course.CoursePageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.course.CoursePageQueryRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 课程业务逻辑实现层
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseDO> implements CourseService {

    private final CollegeService collegeService;

    private final MajorService majorService;
    private final MajorMapper majorMapper;

    @Override
    public IPage<CoursePageQueryRespDTO> pageQueryCourse(CoursePageQueryReqDTO requestParam) {

        // 单独写个mapper，需要判断传进来的是id还是name，二选一
        // 根据id/name 查询完整的name信息
        return baseMapper.pageCourseResults(requestParam);
    }

    @Override
    public void createCourse(CourseSaveReqDTO requestParam) {

        CourseDO courseDO = BeanUtil.toBean(requestParam, CourseDO.class);
        courseDO.setId(IdUtil.getSnowflakeNextId());

        try {
            baseMapper.insert(courseDO);
        } catch (DuplicateKeyException ex) {
            throw new ClientException(ex.getMessage());
        }

    }

    @Override
    public void deleteCourse(Long id) {
        LambdaUpdateWrapper<CourseDO> updateWrapper = Wrappers.lambdaUpdate(CourseDO.class)
                .eq(CourseDO::getId, id)
                .eq(CourseDO::getDelFlag, 0)
                .set(CourseDO::getDelFlag, 1);
        baseMapper.update(updateWrapper);
    }

    @Override
    public void updateCourse(CourseUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<CourseDO> updateWrapper = Wrappers.lambdaUpdate(CourseDO.class)
                .eq(CourseDO::getId, requestParam.getId())
                .set(StrUtil.isNotBlank(requestParam.getCourseName()), CourseDO::getCourseName, requestParam.getCourseName())
                .set(StrUtil.isNotBlank(requestParam.getDictId()), CourseDO::getDictId, requestParam.getDictId())
                .set(StrUtil.isNotBlank(String.valueOf(requestParam.getCourseNature())), CourseDO::getCourseNature, requestParam.getCourseNature());

        int update = baseMapper.update(updateWrapper);
        if (!SqlHelper.retBool(update)) {
            throw new ClientException("更新课程信息失败：课程不存在");
        }
    }

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