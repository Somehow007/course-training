package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateReqDTO;

/**
 * 培养计划服务业务逻辑层
 */
public interface TrainingProgramService extends IService<TrainingProgramDO> {

    /**
     * 创建培养计划
     *
     * @param requestParam 请求参数
     */
    void createTrainingProgram(TrainingProgramCreateReqDTO requestParam);

    /**
     * 为培养计划添加课程
     *
     * @param requestParam  请求参数
     */
    void addCourseToTrainingProgram(TrainingProgramAddCourseReqDTO requestParam);

    /**
     * 修改培养计划课程信息
     *
     * @param requestParam 培养计划参数
     */
    void updateCourseToTrainingProgram(TrainingProgramUpdateCourseReqDTO requestParam);

    /**
     * 删除培养计划
     *
     * @param id 培养计划 Id
     */
    void deleteTrainingProgram(String id);

    /**
     * 修改培养计划
     *
     * @param requestParam 培养计划参数
     */
    void updateTrainingProgram(TrainingProgramUpdateReqDTO requestParam);
}
