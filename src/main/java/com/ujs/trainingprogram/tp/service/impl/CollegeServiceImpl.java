package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.CollegePageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 学院业务逻辑实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, CollegeDO> implements CollegeService {

    private final RedissonClient redissonClient;

    private CollegeMapper collegeMapper;

//    @Autowired
//    @Lazy
    private MajorService majorService;

//    @Autowired
//    @Lazy
    private CourseService courseService;

    @Override
    public String getMaxCollegeId() {
        return getBaseMapper().getMaxCollegeId();
    }

    @Override
    public void createCollege(CollegeSaveReqDTO requestParam) {
        CollegeDO collegeDO = BeanUtil.toBean(requestParam, CollegeDO.class);
        collegeMapper.insert(collegeDO);
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
            redissonClient.getReadWriteLock(String.format())
        }
    }


    @Override
    public List<CollegeDO> getCollegeLikeByName(String collegeName) {
        QueryWrapper<CollegeDO> wrapper = new QueryWrapper<>();
        wrapper.like("college_name", collegeName);
        return getBaseMapper().selectList(wrapper);
    }


    @Override
    public void countAll() {
        List<CollegeDO> list = getBaseMapper().selectList(new QueryWrapper<>());
        list.forEach(college ->
                college.setCourseNum(courseService.selectCountWithCollege(college.getCollegeId())));
        saveOrUpdateBatch(list);
    }

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

    /**
     * 修改学院课程数量
     * @param collegeId
     * @param num
     */
    @Override
    public void modifyCourseNum(String collegeId, int num) {
        CollegeDO collegeDO = getById(collegeId);
        collegeDO.setCourseNum(collegeDO.getCourseNum() + num);
        saveOrUpdate(collegeDO);
    }


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

//    @Override
//    public List<College> getCollegeNameAndCourseNum() {
//        QueryWrapper<College> wrapper = new QueryWrapper<>();
//        wrapper.select("college_name", "course_num");
//        return getBaseMapper().selectList(wrapper);
//    }
}
