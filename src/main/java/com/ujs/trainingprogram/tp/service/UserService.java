package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dto.req.user.ResetPasswordReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserRegistryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.user.UserPageQueryRespDTO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户信息业务逻辑层
 */
public interface UserService extends IService<UserDO> {

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

    /**
     * 删除用户信息（软删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 启用用户信息
     *
     * @param id 用户ID
     */
    void enableUser(Long id);

    /**
     * 重置用户密码
     *
     * @param requestParam 重置密码请求参数
     * @param request HTTP请求对象
     */
    void resetPassword(ResetPasswordReqDTO requestParam, HttpServletRequest request);
}
