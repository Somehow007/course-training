package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ujs.trainingprogram.tp.common.constant.RedisKeyConstant;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dto.req.course.CoursePageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.course.CoursePageQueryRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 课程业务逻辑实现层
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseDO> implements CourseService {

    private final CollegeService collegeService;
    private final StringRedisTemplate stringRedisTemplate;

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
            if (StrUtil.isNotBlank(courseDO.getCourseName()) && courseDO.getId() != null) {
                stringRedisTemplate.opsForHash().put(
                        RedisKeyConstant.COURSE_NAME_ID_KEY,
                        courseDO.getCourseName(),
                        courseDO.getId().toString()
                );
                stringRedisTemplate.expire(RedisKeyConstant.COURSE_NAME_ID_KEY, 7, TimeUnit.DAYS);
            }
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
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.COURSE_NAME_ID_KEY);
        ids.forEach(each -> {
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                if (entry.getValue() != null && entry.getValue().toString().equals(each)) {
                    stringRedisTemplate.opsForHash().delete(RedisKeyConstant.COURSE_NAME_ID_KEY, entry.getKey().toString());
                }
            }
        });

        baseMapper.update(updateWrapper);
    }

    @Override
    public void enableCourse(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ClientException("启用失败：课程 ID 列表不能为空");
        }

        List<Long> longIds = ids.stream()
                .map(Long::parseLong)
                .toList();

        LambdaUpdateWrapper<CourseDO> updateWrapper = Wrappers.lambdaUpdate(CourseDO.class)
                .in(CourseDO::getId, longIds)
                .eq(CourseDO::getDelFlag, 1)
                .set(CourseDO::getDelFlag, 0);
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
        if (StrUtil.isNotBlank(requestParam.getCourseName())) {
            String oldCourseName = getOldCourseName(requestParam.getId());
            if (StrUtil.isNotBlank(oldCourseName) && !oldCourseName.equals(requestParam.getCourseName())) {
                stringRedisTemplate.opsForHash().delete(RedisKeyConstant.COURSE_NAME_ID_KEY, oldCourseName);
            }
            stringRedisTemplate.opsForHash().put(
                    RedisKeyConstant.COURSE_NAME_ID_KEY,
                    requestParam.getCourseName(),
                    requestParam.getId()
            );
            stringRedisTemplate.expire(RedisKeyConstant.COURSE_NAME_ID_KEY, 7, java.util.concurrent.TimeUnit.DAYS);
            int update = baseMapper.update(updateWrapper);
            if (!SqlHelper.retBool(update)) {
                throw new ClientException("更新课程信息失败：课程不存在");
            }
        }
    }

    @Override
    public List<CourseDO> listCourses() {
        LambdaQueryWrapper<CourseDO> queryWrapper = Wrappers.lambdaQuery(CourseDO.class)
                .eq(CourseDO::getDelFlag, 0);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 从缓存中获取旧的课程名称（用于更新时删除旧记录）
     */
    private String getOldCourseName(String courseId) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisKeyConstant.COURSE_NAME_ID_KEY);
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            if (entry.getValue() != null && entry.getValue().toString().equals(courseId)) {
                return entry.getKey().toString();
            }
        }
        return null;
    }


}