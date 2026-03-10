package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramSelectRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养计划数据库持久层
 */
public interface TrainingProgramMapper extends BaseMapper<TrainingProgramDO> {

//    /**
//     * 根据学院与专业信息查找培养计划
//     *
//     * @param collegeId 学院ID
//     * @param majorId   专业ID
//     * @return          培养计划基础信息
//     */
//    TrainingProgramSelectRespDTO selectByCollegeAndMajor(@Param("collegeId") Long collegeId, @Param("majorId") Long majorId);
}
