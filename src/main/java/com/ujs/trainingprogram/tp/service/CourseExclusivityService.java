package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.CourseExclusivityDO;
import com.ujs.trainingprogram.tp.dao.entity.CourseExclusivityDetailDO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivitySaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.courseexclusivity.CourseToExclusivityRespDTO;

import java.util.List;
import java.util.Map;

/**
 * 课程分组接口层
 */
public interface CourseExclusivityService extends IService<CourseExclusivityDO> {

    /**
     * 添加新的课程分组
     *
     * @param requestParam  添加新课程分组的请求参数实体
     */
    Long createCourseExclusivity(CourseExclusivitySaveReqDTO requestParam);

    /**
     * 根据id删除课程分组
     *
     * @param ids 培养计划id
     */
    void deleteCourseExclusivity(List<String> ids);

    /**
     * 根据id删除课程分组详细信息
     *
     * @param ids 课程分组id
     */
    void deleteCourseExclusivityDetail(List<String> ids);

    /**
     * 更新课程分组
     *
     * @param requestParam  请求参数
     */
    void updateCourseExclusivity(CourseExclusivityUpdateReqDTO requestParam);

    /**
     * 查询所有课程分组
     *
     * @return  所有课程
     */
    List<CourseExclusivityDO> listCourseExclusivity();

    /**
     * 为课程分组添加课程信息
     *
     * @param requestParam  请求参数
     */
    void addCourseToCourseExclusivity(CourseExclusivityAddCourseReqDTO requestParam);

    /**
     * 为课程分组批量添加课程信息
     *
     * @param requestParams  请求参数
     */
    void batchAddCourseToCourseExclusivity(List<CourseExclusivityAddCourseReqDTO> requestParams);

    /**
     * 根据培养计划 ID 查询课程分组
     *
     * @param id    培养计划分组
     * @return      课程分组
     */
    List<CourseExclusivityDO> selectByTpId(String id);

    /**
     * 根据培养计划 Id 获取分组编码以及对应的课程
     *
     * @param id    培养计划 Id
     * @return      分组编码
     */
    Map<String, List<String>> getElectiveGroupCode(String id);

    /**
     * 根据培养计划 Id (Key) 获取课程分组 (value)
     *
     * @param id    培养计划 Id
     * @return      课程分组
     */
    List<CourseExclusivityDO> selectAllByTpId(String id);

    /**
     * 根据培养计划 Id 获取课程及其分组情况
     *
     * @param id    培养计划 Id
     * @return      key 课程名称 value 返回体
     */
    Map<String, CourseToExclusivityRespDTO> selectCourseToExclusivity(String id);


//
//    /**
//     * 修改课程分组信息
//     *
//     * @param requestParam 培养计划参数
//     */
//    void updateCourseToCourseExclusivity(CourseExclusivityUpdateCourseReqDTO requestParam);
}
