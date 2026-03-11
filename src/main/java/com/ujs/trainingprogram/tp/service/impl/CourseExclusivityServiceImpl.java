package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.*;
import com.ujs.trainingprogram.tp.dao.entity.CourseExclusivityDO;
import com.ujs.trainingprogram.tp.dao.mapper.CourseExclusivityDetailMapper;
import com.ujs.trainingprogram.tp.dao.mapper.CourseExclusivityMapper;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivitySaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityUpdateReqDTO;
import com.ujs.trainingprogram.tp.service.CourseExclusivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程分组逻辑实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseExclusivityServiceImpl extends ServiceImpl<CourseExclusivityMapper, CourseExclusivityDO> implements CourseExclusivityService {

    private final CourseMapper courseMapper;
    private final CourseExclusivityDetailMapper courseExclusivityDetailMapper;
//    private static final String GROUP_CODE_SUFFIX = "EXC_GROUP_%s_%d_"
    

    @Override
    public Long createCourseExclusivity(CourseExclusivitySaveReqDTO requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new ClientException("创建培养计划失败，请确认传入的参数有效");
        }
        CourseExclusivityDO courseExclusivityDO = BeanUtil.toBean(requestParam, CourseExclusivityDO.class);
        courseExclusivityDO.setId(IdUtil.getSnowflakeNextId());
        courseExclusivityDO.setVersion(1);
        baseMapper.insert(courseExclusivityDO);

        return courseExclusivityDO.getId();
    }

    @Override
    public void deleteCourseExclusivity(List<String> ids) {
        LambdaUpdateWrapper<CourseExclusivityDO> updateWrapper = Wrappers.lambdaUpdate(CourseExclusivityDO.class)
                .in(CourseExclusivityDO::getId, ids)
                .eq(CourseExclusivityDO::getDelFlag, 0)
                .set(CourseExclusivityDO::getDelFlag, 1);
        baseMapper.update(updateWrapper);
    }

    @Override
    public void deleteCourseExclusivityDetail(List<String> ids) {
        LambdaUpdateWrapper<CourseExclusivityDetailDO> updateWrapper = Wrappers.lambdaUpdate(CourseExclusivityDetailDO.class)
                .in(CourseExclusivityDetailDO::getExclusivityId, ids)
                .eq(CourseExclusivityDetailDO::getDelFlag, 0)
                .set(CourseExclusivityDetailDO::getDelFlag, 1);
        courseExclusivityDetailMapper.update(updateWrapper);
    }

    @Override
    public void updateCourseExclusivity(CourseExclusivityUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<CourseExclusivityDO> updateWrapper = Wrappers.lambdaUpdate(CourseExclusivityDO.class)
                .eq(CourseExclusivityDO::getDelFlag, 0)
                .eq(CourseExclusivityDO::getId, requestParam.getExclusivityId())
                .set(StrUtil.isNotBlank(requestParam.getRequiredCredits()), CourseExclusivityDO::getRequiredCredits, requestParam.getRequiredCredits());
        baseMapper.update(updateWrapper);
    }

    @Override
    public List<CourseExclusivityDO> listCourseExclusivity() {
        return List.of();
    }

    @Override
    public void addCourseToCourseExclusivity(CourseExclusivityAddCourseReqDTO requestParam) {
        if (StrUtil.isBlank(requestParam.getExclusivityId()) || StrUtil.isBlank(requestParam.getCourseId())) {
            throw new ClientException("添加课程至课程分组中失败，请传入待添加的课程分组与课程信息");
        }

        CourseExclusivityDO courseExclusivityDO = baseMapper.selectById(requestParam.getExclusivityId());
        if (Objects.isNull(courseExclusivityDO)) {
            throw new ClientException("添加课程至课程分组中失败，该课程分组不存在");
        }

        CourseDO courseDO = courseMapper.selectById(requestParam.getCourseId());
        if (Objects.isNull(courseDO)) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的课程");
        }

        CourseExclusivityDetailDO courseExclusivityDetailDO = BeanUtil.toBean(requestParam, CourseExclusivityDetailDO.class);
        courseExclusivityDetailDO.setId(IdUtil.getSnowflakeNextId());

        courseExclusivityDetailMapper.insert(courseExclusivityDetailDO);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddCourseToCourseExclusivity(List<CourseExclusivityAddCourseReqDTO> requestParams) {
        if (CollUtil.isEmpty(requestParams)) {
            throw new ClientException("批量添加失败: 参数列表不能为空");
        }

        // 1. 提取所有的 exclusivityId 和 courseId，一次性查询验证
        Set<String> exclusivityIds = new HashSet<>();
        Set<String> courseIds = new HashSet<>();

        for (CourseExclusivityAddCourseReqDTO param : requestParams) {
            if (StrUtil.isBlank(param.getExclusivityId()) || StrUtil.isBlank(param.getCourseId())) {
                throw new ClientException("批量添加失败：第 " + (requestParams.indexOf(param) + 1) + " 条记录的课程分组 ID 或课程 ID 为空");
            }
            exclusivityIds.add(param.getExclusivityId());
            courseIds.add(param.getCourseId());
        }

        // 2. 批量查询，验证课程分组是否存在
        List<CourseExclusivityDO> exclusivities = baseMapper.selectBatchIds(exclusivityIds);
        if (exclusivities.size() != exclusivityIds.size()) {
            Set<String> existingIds = exclusivities.stream()
                    .map(CourseExclusivityDO::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toSet());

            Set<String> notFoundIds = exclusivityIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toSet());

            throw new ClientException("批量添加失败：以下课程分组不存在：" + String.join(", ", notFoundIds));
        }

        // 3. 批量查询，验证课程是否存在
        List<CourseDO> courses = courseMapper.selectBatchIds(courseIds);
        if (courses.size() != courseIds.size()) {
            Set<String> existingIds = courses.stream()
                    .map(CourseDO::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toSet());

            Set<String> notFoundIds = courseIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toSet());

            throw new ClientException("批量添加失败：以下课程不存在：" + String.join(", ", notFoundIds));
        }

        // 4. 转换为实体对象列表
        List<CourseExclusivityDetailDO> details = requestParams.stream()
                .map(param -> CourseExclusivityDetailDO.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .exclusivityId(Long.parseLong(param.getExclusivityId()))
                        .courseId(Long.parseLong(param.getCourseId()))
                        .build())
                .collect(Collectors.toList());

        // 5. 批量插入（使用 MyBatis-Plus 的 saveBatch）
        int successCount = courseExclusivityDetailMapper.insertBatch(details);

        if (successCount <= 0) {
            throw new ClientException("批量添加课程至课程分组失败");
        }
        log.info("批量添加成功，共插入 {} 条记录", successCount);
    }

    @Override
    public CourseExclusivityDO selectByTpId(String id) {
        LambdaQueryWrapper<CourseExclusivityDO> queryWrapper = Wrappers.lambdaQuery(CourseExclusivityDO.class)
                .eq(CourseExclusivityDO::getTrainingProgramId, id)
                .eq(CourseExclusivityDO::getDelFlag, 0);
        return baseMapper.selectOne(queryWrapper);
    }
}
