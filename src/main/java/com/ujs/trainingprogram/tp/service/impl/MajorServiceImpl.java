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
import com.ujs.trainingprogram.tp.common.exception.ServiceException;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.dao.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorSaveReqDTO;
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
    public String getMaxMajorId(QueryWrapper<MajorDO> wrapper) {
        return getBaseMapper().getMaxMajorId(wrapper);
    }

    @Override
    public List<MajorDO> getMajorLikeByName(String majorName) {
        QueryWrapper<MajorDO> wrapper = new QueryWrapper<>();
        wrapper.like("major_name", majorName);
        return getBaseMapper().selectList(wrapper);
    }

    // 此处的返回值中有collegeName，需要查询数据库，这样是否会造成性能问题？
    // 采用 join 的方式，避免性能杀手
    @Override
    public IPage<MajorPageRespDTO> pageMajors(MajorPageReqDTO requestParam) {
        Page<MajorPageRespDTO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        return baseMapper.pageMajorResults(page, requestParam);
    }


    @Override
    public void createMajor(MajorSaveReqDTO requestParam) {
        LambdaQueryWrapper<CollegeDO> queryWrapper = Wrappers.lambdaQuery(CollegeDO.class)
                .eq(CollegeDO::getCollegeCode, requestParam.getCollegeCode())
                .eq(CollegeDO::getDelFlag, 0);
        CollegeDO collegeDO = collegeMapper.selectOne(queryWrapper);

        MajorDO majorDO = MajorDO.builder()
                .majorName(requestParam.getMajorName())
                .collegeId(collegeDO.getId())
                .majorType(requestParam.getMajorType())
                .majorCode(requestParam.getMajorCode())
                .courseNum(requestParam.getCourseNum())
                .id(IdUtil.getSnowflakeNextId())
                .build();
        try {
            baseMapper.insert(majorDO);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException(String.format("专业编号: %s 重复创建", requestParam.getMajorCode()));
        }
    }

    @Override
    public void deleteMajor(String majorCode) {
        LambdaUpdateWrapper<MajorDO> updateWrapper = Wrappers.lambdaUpdate(MajorDO.class)
                .eq(MajorDO::getMajorCode, majorCode)
                .eq(MajorDO::getDelFlag, 0);
        MajorDO majorDO = new MajorDO();
        majorDO.setDelFlag(1);
        baseMapper.update(majorDO, updateWrapper);
    }

    // todo: 可以删了，因为有分页查询
    @Override
    public List<MajorDO> getMajorByCollegeCode(String collegeCode) {
        LambdaQueryWrapper<CollegeDO> queryWrapper = Wrappers.lambdaQuery(CollegeDO.class)
                .eq(CollegeDO::getCollegeCode, collegeCode)
                .eq(CollegeDO::getDelFlag, 0);
        CollegeDO collegeDO = collegeMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<MajorDO> majorDOQueryWrapper = Wrappers.lambdaQuery(MajorDO.class)
                .eq(MajorDO::getCollegeId, collegeDO.getId())
                .eq(MajorDO::getDelFlag, 0);
        return getBaseMapper().selectList(majorDOQueryWrapper);
    }


    /**
     * 重新计算专业课程总数
     */
    @Override
    public void countAll() {
        List<MajorDO> list = getBaseMapper().selectList(new QueryWrapper<>());
        list.forEach(major -> major.setCourseNum(courseService.selectCountWithMajor(major.getMajorCode())));
        saveOrUpdateBatch(list);
    }

    /**
     * 修改专业课程数量
     * @param majorId
     * @param num
     */
    @Override
    public void modifyCourseNum(String majorId, int num) {
        MajorDO majorDO = getById(majorId);
        majorDO.setCourseNum(majorDO.getCourseNum() + num);
        saveOrUpdate(majorDO);
    }
}
