package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                .eq(CollegeDO::getCollegeCode, requestParam.getOriginCollegeCode())
                .eq(CollegeDO::getDelFlag, 0);
        CollegeDO hasCollegeDO = baseMapper.selectOne(queryWrapper);
        if (hasCollegeDO == null) {
            throw new ClientException("学院不存在");
        }

        LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                .eq(CollegeDO::getCollegeCode, requestParam.getOriginCollegeCode())
                .eq(CollegeDO::getDelFlag, 0);

        CollegeDO collegeDO = CollegeDO.builder()
                .collegeCode(requestParam.getNewCollegeCode())
                .collegeName(requestParam.getCollegeName())
                .courseNum(requestParam.getCourseNum())
                .build();
        baseMapper.update(collegeDO, updateWrapper);

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
                    .map(majorFlat -> {
                        return CollegeMajorPageRespDTO.builder()
                                .majorCode(majorFlat.getMajorCode())
                                .majorName(majorFlat.getMajorName())
                                .courseNum(majorFlat.getCourseNum())
                                .majorType(majorFlat.getMajorType())
                                .build();
                    })
                    .toList();

            return CollegePageRespDTO.builder()
                    .collegeName(first.getCollegeName())
                    .collegeCode(first.getCollegeCode())
                    .majors(majorList)
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
    public void deleteCollege(String collegeCode) {
        LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                .eq(CollegeDO::getCollegeCode, collegeCode)
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
