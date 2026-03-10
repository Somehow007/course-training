package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDetailDO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 培养计划详情数据库持久层
 */
public interface TrainingProgramDetailMapper extends BaseMapper<TrainingProgramDetailDO> {


    /**
     * 查询某专业的培养计划信息
     *
     * @param trainingProgramId 培养计划id
     * @return  返回实体
     */
    List<TrainingProgramDetailSelectRespDTO> selectTrainingProgramDetail(@Param("trainingProgramId") Long trainingProgramId);
}
