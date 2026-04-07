package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramPageReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramPageRespDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 培养计划数据库持久层
 */
public interface TrainingProgramMapper extends BaseMapper<TrainingProgramDO> {

    /**
     * 分页查询培养计划
     *
     * @param page 分页参数
     * @param requestParam 请求实体
     * @return 分页请求结果
     */
    IPage<TrainingProgramPageRespDTO> pageTrainingProgramResults(Page<?> page, @Param("requestParam") TrainingProgramPageReqDTO requestParam);
}
