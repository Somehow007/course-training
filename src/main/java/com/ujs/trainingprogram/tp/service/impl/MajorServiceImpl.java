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
import com.ujs.trainingprogram.tp.common.exception.ServiceException;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.dao.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.major.MajorPageRespDTO;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 专业业务逻辑实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MajorServiceImpl extends ServiceImpl<MajorMapper, MajorDO> implements MajorService {
    @Autowired
    @Lazy
    private CourseService courseService;
    @Autowired
    private CollegeMapper collegeMapper;

    @Override
    public IPage<MajorPageRespDTO> pageMajors(MajorPageReqDTO requestParam) {
        Page<MajorPageRespDTO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        return baseMapper.pageMajorResults(page, requestParam);
    }

    @Override
    public void createMajor(MajorSaveReqDTO requestParam) {
        LambdaQueryWrapper<CollegeDO> queryWrapper = Wrappers.lambdaQuery(CollegeDO.class)
                .eq(CollegeDO::getId, requestParam.getCollegeId())
                .eq(CollegeDO::getDelFlag, 0);
        CollegeDO collegeDO = collegeMapper.selectOne(queryWrapper);
        if (Objects.isNull(collegeDO)) {
            throw new ClientException("添加专业失败：学院不存在");
        }

        MajorDO majorDO = BeanUtil.toBean(requestParam, MajorDO.class);
        majorDO.setId(IdUtil.getSnowflakeNextId());
        try {
            baseMapper.insert(majorDO);
        } catch (DuplicateKeyException ex) {
            throw new ClientException(String.format("专业名称: %s 重复创建", requestParam.getMajorName()));
        }
    }

    @Override
    public void deleteMajor(Long id) {
        LambdaUpdateWrapper<MajorDO> updateWrapper = Wrappers.lambdaUpdate(MajorDO.class)
                .eq(MajorDO::getId, id)
                .eq(MajorDO::getDelFlag, 0);
        MajorDO majorDO = new MajorDO();
        majorDO.setDelFlag(1);
        baseMapper.update(majorDO, updateWrapper);
    }

    @Override
    public void updateMajor(MajorUpdateReqDTO requestParam) {

        // 检查修改到的学院是否存在
        if (StrUtil.isNotBlank(requestParam.getCollegeId())) {
            CollegeDO collegeDO = collegeMapper.selectOne(Wrappers.lambdaQuery(CollegeDO.class)
                    .eq(CollegeDO::getId, requestParam.getCollegeId())
                    .eq(CollegeDO::getDelFlag, 0));
            if (Objects.isNull(collegeDO)) {
                throw new ClientException("更新专业信息失败：更新后的学院不存在");
            }
        }

        LambdaUpdateWrapper<MajorDO> updateWrapper = Wrappers.lambdaUpdate(MajorDO.class)
                .eq(MajorDO::getId, requestParam.getMajorId())
                .eq(MajorDO::getDelFlag, 0)
                .set(StrUtil.isNotBlank(requestParam.getCollegeId()), MajorDO::getCollegeId, requestParam.getCollegeId())
                .set(StrUtil.isNotBlank(requestParam.getMajorName()), MajorDO::getMajorName, requestParam.getMajorName())
                .set(!Objects.isNull(requestParam.getCategory()) &&
                                (requestParam.getCategory() == 0 || requestParam.getCategory() == 1 || requestParam.getCategory() == 2),
                        MajorDO::getCategory, requestParam.getCategory());
        try {
            baseMapper.update(updateWrapper);
        } catch (DuplicateKeyException ex) {
            throw new ClientException(String.format("更新专业信息失败，专业名称: %s 已存在", requestParam.getMajorName()));
        }

    }

}
