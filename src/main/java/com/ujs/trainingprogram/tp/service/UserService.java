package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dto.req.user.UserLoginReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserRegistryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.user.UserPageQueryRespDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户信息业务逻辑层
 */
public interface UserService extends IService<UserDO> {
    public void exportUserToExcel(HttpServletResponse response);

    /**
     * 注册用户信息
     *
     * @param requestParam  注册用户信息请求参数
     */
    void registryUser(UserRegistryReqDTO requestParam);

    /**
     * 分页查询用户信息
     *
     * @param requestParam  请求参数
     * @return 返回结果
     */
    IPage<UserPageQueryRespDTO> pageQueryUser(UserPageQueryReqDTO requestParam);
}
