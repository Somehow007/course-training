package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.common.exception.ServiceException;
import com.ujs.trainingprogram.tp.dao.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.college.CollegePageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.ujs.trainingprogram.tp.common.constant.RedisKeyConstant.LOCK_COLLEGE_NAME_KEY;

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
        baseMapper.insert(collegeDO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCollege(CollegeUpdateReqDTO requestParam) {
        LambdaQueryWrapper<CollegeDO> queryWrapper = Wrappers.lambdaQuery(CollegeDO.class)
                .eq(CollegeDO::getCollegeId, requestParam.getOriginCollegeId())
                .eq(CollegeDO::getDelFlag, 0);
        CollegeDO hasCollegeDO = baseMapper.selectOne(queryWrapper);
        if (hasCollegeDO == null) {
            throw new ClientException("学院不存在");
        }

        if (Objects.equals(hasCollegeDO.getCollegeId(), requestParam.getNewCollegeId())) {
            LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                    .eq(CollegeDO::getCollegeId, requestParam.getNewCollegeId())
                    .eq(CollegeDO::getDelFlag, 0);
            CollegeDO collegeDO = CollegeDO.builder()
                    .collegeId(requestParam.getNewCollegeId())
                    .collegeName(requestParam.getCollegeName())
                    .courseNum(requestParam.getCourseNum())
                    .build();
            baseMapper.update(collegeDO, updateWrapper);
        } else {
            // 此操作为修改学院id，出现此操作的概率相对极小，但不排除有这种可能，也因此此处不能用学院id作为锁key
            // 使用学院名称作为锁key，因为学院名称相对比较稳定
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_COLLEGE_NAME_KEY, requestParam.getCollegeName()));
            RLock rLock = readWriteLock.writeLock();
            if (!rLock.tryLock()) {
                throw new ServiceException("学院信息正在被修改，请稍后再试...");
            }
            try {
                LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                        .eq(CollegeDO::getCollegeId, hasCollegeDO.getCollegeId())
                        .eq(CollegeDO::getCollegeName, hasCollegeDO.getCollegeName())
                        .eq(CollegeDO::getDelFlag, 0);

                CollegeDO delCollegeDO = CollegeDO.builder().build();
                delCollegeDO.setDelFlag(1);
                baseMapper.update(delCollegeDO, updateWrapper);

                CollegeDO collegeDO = CollegeDO.builder()
                        .collegeId(requestParam.getNewCollegeId())
                        .collegeName(requestParam.getCollegeName())
                        .courseNum(requestParam.getCourseNum())
                        .build();
                baseMapper.insert(collegeDO);

                // todo: 修改其他关联学院id的字段、库表

            } finally {
                rLock.unlock();
            }
        }
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
        IPage<CollegeDO> resultPage = baseMapper.pageResult(requestParam);
        return resultPage.convert(each -> {
            CollegePageRespDTO result = BeanUtil.toBean(each, CollegePageRespDTO.class);
            // todo：加专业
            return result;
        });
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
        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
        wrapper.eq("college_name", collegeName);
        return getBaseMapper().selectOne(wrapper);
    }

    @Override
    public void deleteCollege(String collegeId) {
        LambdaUpdateWrapper<CollegeDO> updateWrapper = Wrappers.lambdaUpdate(CollegeDO.class)
                .eq(CollegeDO::getCollegeId, collegeId)
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
