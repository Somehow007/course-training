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
import java.util.stream.Collectors;

/**
 * 课程业务逻辑实现层
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseDO> implements CourseService {

    private final CollegeService collegeService;

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
            throw new ClientException(String.format("创建失败，课程名称不能重复: %s", requestParam.getCourseName()));
        }

    }

    @Override
    public void deleteCourse(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ClientException("删除失败：课程 ID 列表不能为空");
        }

        List<Long> longIds = ids.stream()
                .map(Long::parseLong)
                .toList();

        LambdaUpdateWrapper<CourseDO> updateWrapper = Wrappers.lambdaUpdate(CourseDO.class)
                .in(CourseDO::getId, longIds)
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
                .set(StrUtil.isNotBlank(requestParam.getCollegeId()), CourseDO::getCollegeId, requestParam.getCollegeId())
                .set(StrUtil.isNotBlank(String.valueOf(requestParam.getCourseNature())), CourseDO::getCourseNature, requestParam.getCourseNature());

        int update = baseMapper.update(updateWrapper);
        if (!SqlHelper.retBool(update)) {
            throw new ClientException("更新课程信息失败：课程不存在");
        }
    }

    @Override
    public List<CourseDO> listCourses() {
        LambdaQueryWrapper<CourseDO> queryWrapper = Wrappers.lambdaQuery(CourseDO.class)
                .eq(CourseDO::getDelFlag, 0);
        return baseMapper.selectList(queryWrapper);
    }

}