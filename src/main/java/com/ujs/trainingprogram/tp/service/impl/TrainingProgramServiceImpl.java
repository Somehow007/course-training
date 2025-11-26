package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDO;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDetailDO;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dao.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.dao.mapper.TrainingProgramDetailMapper;
import com.ujs.trainingprogram.tp.dao.mapper.TrainingProgramMapper;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateReqDTO;
import com.ujs.trainingprogram.tp.service.TrainingProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * 培养计划业务逻辑实现层
 */
@Service
@RequiredArgsConstructor
public class TrainingProgramServiceImpl extends ServiceImpl<TrainingProgramMapper, TrainingProgramDO> implements TrainingProgramService {

    private final TrainingProgramMapper trainingProgramMapper;
    private final CourseMapper courseMapper;
    private final TrainingProgramDetailMapper trainingProgramDetailMapper;
    private final MajorMapper majorMapper;

    @Override
    public void createTrainingProgram(TrainingProgramCreateReqDTO requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new ClientException("创建培养计划失败，请确认传入的参数有效");
        }
        TrainingProgramDO trainingProgramDO = BeanUtil.toBean(requestParam, TrainingProgramDO.class);
        trainingProgramDO.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(trainingProgramDO);
    }

    @Override
    public void addCourseToTrainingProgram(TrainingProgramAddCourseReqDTO requestParam) {
        if (StrUtil.isBlank(requestParam.getCourseId()) || StrUtil.isBlank(requestParam.getTrainingProgramId())) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的培养计划与课程");
        }
        TrainingProgramDO trainingProgramDO = trainingProgramMapper.selectById(requestParam.getTrainingProgramId());
        if (Objects.isNull(trainingProgramDO)) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的培养计划");
        }
        CourseDO courseDO = courseMapper.selectById(requestParam.getCourseId());
        if (Objects.isNull(courseDO)) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的课程");
        }

        TrainingProgramDetailDO trainingProgramDetailDO = BeanUtil.toBean(requestParam, TrainingProgramDetailDO.class);
        trainingProgramDetailDO.setId(IdUtil.getSnowflakeNextId());
        trainingProgramDetailDO.setCourseNature(courseDO.getCourseNature());
        trainingProgramDetailDO.setCourseName(courseDO.getCourseName());

        trainingProgramDetailMapper.insert(trainingProgramDetailDO);

        if (StrUtil.isNotBlank(String.valueOf(requestParam.getMajorId()))) {
            majorMapper.incrementCourseNum(Long.parseLong(requestParam.getMajorId()), 1);
        }
    }

    @Override
    public void updateCourseToTrainingProgram(TrainingProgramUpdateCourseReqDTO requestParam) {
        LambdaQueryWrapper<TrainingProgramDetailDO> queryWrapper = Wrappers.lambdaQuery(TrainingProgramDetailDO.class)
                .eq(TrainingProgramDetailDO::getId, requestParam.getId())
                .eq(TrainingProgramDetailDO::getDelFlag, 0);
        TrainingProgramDetailDO originalTrainingProgramDetailDO = trainingProgramDetailMapper.selectOne(queryWrapper);
        if (Objects.isNull(originalTrainingProgramDetailDO)) {
            throw new ClientException("修改培养计划课程失败，请传入待修改的培养计划课程");
        }

        TrainingProgramDetailDO trainingProgramDetailDO = BeanUtil.toBean(originalTrainingProgramDetailDO, TrainingProgramDetailDO.class);
        BeanUtil.copyProperties(requestParam, trainingProgramDetailDO);
        validateTimeUnits(trainingProgramDetailDO);

        // 如果专业更改了修改专业中课程
        if (!Objects.equals(requestParam.getMajorId(), String.valueOf(originalTrainingProgramDetailDO.getMajorId())) &&
                StrUtil.isNotBlank(String.valueOf(requestParam.getMajorId()))) {
            int isDecrementSuccess = majorMapper.decrementCourseNum(originalTrainingProgramDetailDO.getMajorId(), 1);
            int isIncrementSuccess = majorMapper.incrementCourseNum(Long.parseLong(requestParam.getMajorId()), 1);
            if (isIncrementSuccess <= 0 || isDecrementSuccess <= 0) {
                throw new ClientException("课程信息更新失败：专业课程数量修改失败");
            }
        }
    }

    private void validateTimeUnits(TrainingProgramDetailDO requestParam) {
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
    public void deleteTrainingProgram(String id) {
        LambdaUpdateWrapper<TrainingProgramDO> updateWrapper = Wrappers.lambdaUpdate(TrainingProgramDO.class)
                .eq(TrainingProgramDO::getId, id)
                .eq(TrainingProgramDO::getDelFlag, 0)
                .set(TrainingProgramDO::getDelFlag, 1);
        trainingProgramMapper.update(updateWrapper);
    }

    @Override
    public void updateTrainingProgram(TrainingProgramUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<TrainingProgramDO> updateWrapper = Wrappers.lambdaUpdate(TrainingProgramDO.class)
                .eq(TrainingProgramDO::getId, requestParam.getId())
                .set(StrUtil.isNotBlank(requestParam.getName()), TrainingProgramDO::getName, requestParam.getName())
                .set(StrUtil.isNotBlank(requestParam.getYear()), TrainingProgramDO::getYear, requestParam.getYear())
                .set(StrUtil.isNotBlank(requestParam.getDescription()), TrainingProgramDO::getDescription, requestParam.getDescription());
        trainingProgramMapper.update(updateWrapper);
    }


}
