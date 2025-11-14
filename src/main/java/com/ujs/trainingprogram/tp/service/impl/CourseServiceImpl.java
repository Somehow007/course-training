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

        validateTimeUnits(courseDO);

        try {
            baseMapper.insert(courseDO);
        } catch (DuplicateKeyException ex) {
            throw new ClientException(ex.getMessage());
        }

        // 如果是专业课，让专业课的数量增加
        if (StrUtil.isNotBlank(String.valueOf(requestParam.getMajorId()))) {
            majorMapper.incrementCourseNum(requestParam.getMajorId(), 1);
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
        LambdaQueryWrapper<CourseDO> queryWrapper = Wrappers.lambdaQuery(CourseDO.class)
                .eq(CourseDO::getId, requestParam.getCourseId())
                .eq(CourseDO::getDelFlag, 0);
        CourseDO originalCourseDO = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(originalCourseDO)) {
            throw new ClientException("课程信息更新失败：不存在该课程");
        }

        CourseDO courseDO = BeanUtil.toBean(requestParam, CourseDO.class);
        courseDO.setId(Long.parseLong(requestParam.getCourseId()));
        validateTimeUnits(courseDO);

        if (!Objects.equals(requestParam.getMajorId(), originalCourseDO.getMajorId()) &&
                StrUtil.isNotBlank(String.valueOf(requestParam.getMajorId()))) {
            int isDecrementSuccess = majorMapper.decrementCourseNum(originalCourseDO.getMajorId(), 1);
            int isIncrementSuccess = majorMapper.incrementCourseNum(requestParam.getMajorId(), 1);
            if (isIncrementSuccess <= 0 || isDecrementSuccess <= 0) {
                throw new ClientException("课程信息更新失败：专业课程数量修改失败");
            }
        }

        LambdaUpdateWrapper<CourseDO> updateWrapper = Wrappers.lambdaUpdate(CourseDO.class)
                .eq(CourseDO::getId, requestParam.getCourseId())
                .eq(CourseDO::getDelFlag, 0);
        baseMapper.update(courseDO, updateWrapper);
    }

    private void validateTimeUnits(CourseDO requestParam) {
        boolean hasTotalHours = Optional.ofNullable(requestParam.getTotalHours()).orElse(0f) > 0;
        boolean hasTotalWeeks = Optional.ofNullable(requestParam.getTotalWeeks()).orElse(0f) > 0;
        if (hasTotalWeeks == hasTotalHours) {
            throw new ClientException("创建课程失败，totalHours 和 totalWeeks 必须有且只有一个有值");
        }

        if (hasTotalHours) {
            requestParam.setHoursUnit(0);
        }
        if (hasTotalWeeks) {
            requestParam.setHoursUnit(1);
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