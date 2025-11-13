package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.major.MajorPageRespDTO;

import java.util.List;

/**
 * 专业业务逻辑接口层
 */
public interface MajorService extends IService<MajorDO> {

    String getMaxMajorId(QueryWrapper<MajorDO> wrapper);

    List<MajorDO> getMajorLikeByName(String majorName);

    /**
     * 分页查询专业
     *
     * @param requestParam 分页查询专业请求实体
     * @return 分页查询返回数据
     */
    IPage<MajorPageRespDTO> pageMajors(MajorPageReqDTO requestParam);

    /**
     * 创建专业
     *
     * @param requestParam 创建专业请求参数
     */
    void createMajor(MajorSaveReqDTO requestParam);

    /**
     * 删除专业
     *
     * @param id 专业编号
     */
    void deleteMajor(Long id);

    /**
     * 更新专业
     *
     * @param requestParam 更新专业请求参数
     */
    void updateMajor(MajorUpdateReqDTO requestParam);

    void countAll();
    void modifyCourseNum(String majorId, int num);

    List<MajorDO> getMajorByCollegeCode(String collegeCode);
}
