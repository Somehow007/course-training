package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.CourseExclusivityDO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivitySaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateCourseReqDTO;

import java.util.List;

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
     * @param ids 课程分组id
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
    CourseExclusivityDO selectByTpId(String id);

//
//    /**
//     * 修改课程分组信息
//     *
//     * @param requestParam 培养计划参数
//     */
//    void updateCourseToCourseExclusivity(CourseExclusivityUpdateCourseReqDTO requestParam);
}
