package com.ujs.trainingprogram.tp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dto.req.user.UserPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.user.UserPageQueryRespDTO;

/**
 * 用户信息数据库持久层
 */
public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 分页统计用户信息
     */
    IPage<UserPageQueryRespDTO> pageUserResults(UserPageQueryReqDTO requestParam);
}
