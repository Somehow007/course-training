package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.*;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramPageRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramSelectRespDTO;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 培养计划服务业务逻辑层
 */
public interface TrainingProgramService extends IService<TrainingProgramDO> {

    /**
     * 分页查询培养计划
     *
     * @param requestParam 请求参数
     * @return 分页结果
     */
    IPage<TrainingProgramPageRespDTO> pageTrainingPrograms(TrainingProgramPageReqDTO requestParam);

    /**
     * 创建培养计划
     *
     * @param requestParam 请求参数
     */
    void createTrainingProgram(TrainingProgramCreateReqDTO requestParam);

    /**
     * 创建培养计划
     *
     * @param requestParam 请求参数
     * @return             培养计划ID
     */
    Long createTrainingProgram(TrainingProgramDO requestParam);

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
     * 批量删除某专业的培养计划数据
     *
     * @param id 培养计划 ID
     */
    void deleteTrainingProgramDetails(String id);

    /**
     * 修改培养计划
     *
     * @param requestParam 培养计划参数
     */
    void updateTrainingProgram(TrainingProgramUpdateReqDTO requestParam);

    /**
     * 查询某专业的培养计划信息
     *
     * @param id 培养计划的Id
     * @return  返回信息
     */
    List<TrainingProgramDetailSelectRespDTO> selectTrainingProgramDetail(String id);

    /**
     * 导出培养计划到Excel
     *
     * @param id 培养计划ID
     * @param response HTTP响应对象
     */
    void exportTrainingProgramToExcel(String id, HttpServletResponse response);

    /**
     * 从Excel导入培养计划
     *
     * @param file Excel文件
     */
    void importTrainingProgramFromExcel(MultipartFile file, String collegeId, String majorId);
    
    /**
     * 下载培养计划Excel模板
     *
     * @param response HTTP响应对象
     */
    void downloadTrainingProgramTemplate(HttpServletResponse response);
    
    /**
     * 从 Excel 中批量保存培养计划详情
     *
     * @param dataList Excel数据列表
     */
    void batchSaveTrainingProgramDetailsFromExcel(List<TrainingProgramExcelTemplate> dataList, Long tpId, Long majorId, Integer version);

    /**
     * 批量保存培养计划详情
     *
     * @param requestParams 请求参数
     */
    void batchSaveTrainingProgramDetails(List<TrainingProgramAddCourseFromExcelReqDTO> requestParams, Integer version);

    /**
     * 根据学院与专业信息查找培养计划
     *
     * @param collegeId 学院ID
     * @param majorId   专业ID
     * @return          培养计划基础信息
     */
    TrainingProgramSelectRespDTO selectTrainingProgramByCollegeAndMajor(String collegeId, String majorId);
}