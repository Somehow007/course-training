package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.ujs.trainingprogram.tp.dao.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dto.CollegePageMajorDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeMajorPageRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.college.CollegePageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学院业务逻辑实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, CollegeDO> implements CollegeService {

    private final RedissonClient redissonClient;

    @Override
    public String getMaxCollegeId() {
        return getBaseMapper().getMaxCollegeId();
    }

    @Override
    public void createCollege(CollegeSaveReqDTO requestParam) {
        CollegeDO collegeDO = BeanUtil.toBean(requestParam, CollegeDO.class);
        collegeDO.setId(IdUtil.getSnowflakeNextId());
        try {
            baseMapper.insert(collegeDO);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException(String.format("学院编号: %s 重复创建", requestParam.getCollegeCode()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCollege(CollegeUpdateReqDTO requestParam) {
        LambdaQueryWrapper<CollegeDO> queryWrapper = Wrappers.lambdaQuery(CollegeDO.class)
                .eq(CollegeDO::getCollegeCode, requestParam.getCollegeCode());

        CollegeDO collegeDO = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(collegeDO)) {
            throw new ClientException("学院信息更新失败：该学院信息检查不到");
        }
        LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                .eq(CollegeDO::getCollegeCode, requestParam.getCollegeCode())
                .eq(CollegeDO::getDelFlag, 0);

        CollegeDO collegeDO1 = CollegeDO.builder()
                .collegeName(StrUtil.isNotBlank(requestParam.getCollegeName()) ? requestParam.getCollegeName() : collegeDO.getCollegeName())
                .build();
        baseMapper.update(collegeDO1, updateWrapper);

    }


    @Override
    public List<CollegeDO> getCollegeLikeByName(String collegeName) {
        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
        wrapper.like("college_name", collegeName);
        return getBaseMapper().selectList(wrapper);
    }


//    @Override
//    public void countCollegeCourseNums() {
//        List<CollegeDO> list = getBaseMapper().selectList(new QueryWrapper<>());
//        list.forEach(college ->
//                college.setCourseNum(courseService.selectCountWithCollege(college.getCollegeId())));
//        saveOrUpdateBatch(list);
//    }

    @Override
    public IPage<CollegePageRespDTO> pageCollege(CollegePageReqDTO requestParam) {
        Page<CollegeDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        IPage<CollegePageMajorDTO> flagPage = baseMapper.pageCollegeResult(page, requestParam);

        List<CollegePageRespDTO> aggregatedList = aggregateToTree(flagPage.getRecords());
        return new Page<CollegePageRespDTO>()
                .setCurrent(flagPage.getCurrent())
                .setSize(flagPage.getSize())
                .setTotal(flagPage.getTotal())
                .setPages(flagPage.getPages())
                .setRecords(aggregatedList);
    }

    private List<CollegePageRespDTO> aggregateToTree(List<CollegePageMajorDTO> flatList) {
        if (CollectionUtil.isEmpty(flatList)) {
            return new ArrayList<>();
        }

        // 按学院分组，使用两个字段作为唯一键
        Map<String, List<CollegePageMajorDTO>> groupByCollege = flatList.stream()
                .collect(Collectors.groupingBy(dto ->
                        dto.getCollegeCode() + ":" + dto.getCollegeName()
                ));
        return groupByCollege.values().stream().map(majors -> {
            CollegePageMajorDTO first = majors.get(0);

            List<CollegeMajorPageRespDTO> majorList = majors.stream()
                    .map(majorFlat -> CollegeMajorPageRespDTO.builder()
                            .majorCode(majorFlat.getMajorCode())
                            .majorName(majorFlat.getMajorName())
                            .courseNum(majorFlat.getCourseNum())
                            .categoryId(majorFlat.getCategoryId())
                            .build())
                    .toList();

            // 计算专业数量
            int majorNum = majors.size();

            // 计算学院总课程数
            int totalCourseNum = majors.stream()
                    .mapToInt(major -> Optional.ofNullable(major.getCourseNum()).orElse(0))
                    .sum();

            return CollegePageRespDTO.builder()
                    .id(first.getId())
                    .collegeName(first.getCollegeName())
                    .majors(majorList)
                    .majorNum(majorNum)
                    .courseNum(totalCourseNum)
                    .build();
        }).collect(Collectors.toList());
    }

//    /**
//     * 自定义查询
//     * @param cur
//     * @param size
//     * @param wrapper
//     * @return
//     */
//    @Override
//    public ResultData (long cur, long size, QueryWrapper<CollegeDO> wrapper) {
//        Page<CollegeDO> page = new Page<>(cur, size);
//        Page<CollegeDO> collegePage = getBaseMapper().selectPage(page, wrapper);
//        ResultData resultData = new ResultData();
//        List<CollegeDO> records = collegePage.getRecords();
//        records.forEach(college -> {
//            List<MajorDO> majorDOS = majorService.getMajorByCollegeId(college.getCollegeId());
//            college.setCourseNum(courseService.selectCountWithCollege(college.getCollegeId()));
//        });
//        resultData.setData(records);
//        resultData.setCur(collegePage.getCurrent());
//        resultData.setSize(collegePage.getSize());
//        resultData.setTotal(collegePage.getTotal());
//
//        return resultData;
//    }

    @Override
    public List<CollegeDO> getCollegeNameAndId() {
        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
        wrapper.select("college_id", "college_name");
        return getBaseMapper().selectList(wrapper);
    }

    @Override
    public CollegeDO getCollegeByName(String collegeName) {
        LambdaQueryWrapper<CollegeDO> queryWrapper = Wrappers.lambdaQuery(CollegeDO.class)
                .like(CollegeDO::getCollegeName, collegeName)
                .eq(CollegeDO::getDelFlag, 0);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void deleteCollege(Long id) {
        LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                .eq(CollegeDO::getId, id)
                .eq(CollegeDO::getDelFlag, 0);
        CollegeDO collegeDO = new CollegeDO();
        collegeDO.setDelFlag(1);
        baseMapper.update(collegeDO, updateWrapper);
    }

//    @Override
//    public List<College> getCollegeNameAndCourseNum() {
//        QueryWrapper<College> wrapper = new QueryWrapper<>();
//        wrapper.select("college_name", "course_num");
//        return getBaseMapper().selectList(wrapper);
//    }
}
