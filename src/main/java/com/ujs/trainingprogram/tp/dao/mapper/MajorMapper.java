package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.major.MajorPageRespDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.toolkit.Constants;

/**
 * 专业管理持久层
 */
public interface MajorMapper extends BaseMapper<MajorDO> {
    @Select("SELECT MAX(major_id) FROM major #{ew.customSqlSegment}")
    String getMaxMajorId(@Param(Constants.WRAPPER)QueryWrapper<MajorDO> wrapper);

    /**
     * 分页查询专业信息
     *
     * @param page 分页参数
     * @param requestParam  请求实体
     * @return 分页请求结果
     */
    IPage<MajorPageRespDTO> pageMajorResults(Page<?> page, @Param("requestParam") MajorPageReqDTO requestParam);


    /**
     * 自增专业课程数量
     *
     * @param majorId      专业id
     * @param incrementNum 增加数量
     * @return 是否发生记录变更
     */
    int incrementCourseNum(@Param("majorId") Long majorId, @Param("incrementNum") Integer incrementNum);

    /**
     * 自减专业课程数量
     *
     * @param majorId      专业id
     * @param decrementNum 减少数量
     * @return 是否发生记录变更
     */
    int decrementCourseNum(@Param("majorId") Long majorId, @Param("decrementNum") Integer decrementNum);
}
