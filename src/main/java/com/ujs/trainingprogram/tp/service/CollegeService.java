package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.college.CollegePageRespDTO;

import java.util.List;

/**
 * 学院接口层
 */
public interface CollegeService extends IService<CollegeDO> {
    String getMaxCollegeId();

    /**
     * 创建学院
     *
     * @param requestParam 创建学院请求参数
     */
    void createCollege(CollegeSaveReqDTO requestParam);

    /**
     * 修改学院
     *
     * @param requestParam 修改学院请求参数
     */
    void updateCollege(CollegeUpdateReqDTO requestParam);

    /**
     * 通过学院名称模糊查找
     *
     * @param collegeName 学院名称
     * @return 查询到的学院列表
     */
    List<CollegeDO> getCollegeLikeByName(String collegeName);

//    /**
//     * 获取学院的总课程数
//     */
//    void countCollegeCourseNums();

    /**
     * 学院分页查询
     *
     * @param requestParam 学院分页查询请求参数
     * @return 学院分页查询返回结果
     */
    IPage<CollegePageRespDTO> pageCollege(CollegePageReqDTO requestParam);

    List<CollegeDO> getCollegeNameAndId();

    /**
     * 根据学院名称查询学院信息
     *
     * @param collegeName 查询学院信息请求参数
     * @return 返回实体
     */
    CollegeDO getCollegeByName(String collegeName);

    /**
     * 删除学院
     *
     * @param collegeCode 学院编号
     */
    void deleteCollege(String collegeCode);

//    List<College> getCollegeNameAndCourseNum();
}
