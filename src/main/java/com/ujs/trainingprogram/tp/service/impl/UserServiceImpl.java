package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.OperationLogDO;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dao.mapper.OperationLogMapper;
import com.ujs.trainingprogram.tp.dao.mapper.UserMapper;
import com.ujs.trainingprogram.tp.dto.req.user.ResetPasswordReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserRegistryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.user.UserPageQueryRespDTO;
import com.ujs.trainingprogram.tp.security.SecurityUserDetails;
import com.ujs.trainingprogram.tp.service.UserService;
import com.ujs.trainingprogram.tp.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void registryUser(UserRegistryReqDTO requestParam) {
        if (StrUtil.isBlank(requestParam.getUsername()) || StrUtil.isBlank(requestParam.getPassword())) {
            throw new ClientException("注册用户失败：请确认用户名或密码是否填写");
        }
        UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
        userDO.setId(IdUtil.getSnowflakeNextId());
        userDO.setPassword(passwordEncoder.encode(requestParam.getPassword()));
        try {
            baseMapper.insert(userDO);
        } catch (DuplicateKeyException ex) {
            throw new ClientException("注册失败: 用户名已存在");
        }
    }

    @Override
    public IPage<UserPageQueryRespDTO> pageQueryUser(UserPageQueryReqDTO requestParam) {
        return baseMapper.pageUserResults(requestParam);
    }

    @Override
    public void deleteUser(Long id) {
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getId, id)
                .eq(UserDO::getDelFlag, 0)
                .set(UserDO::getDelFlag, 1);
        baseMapper.update(updateWrapper);
    }

    @Override
    public void enableUser(Long id) {
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getId, id)
                .eq(UserDO::getDelFlag, 1)
                .set(UserDO::getDelFlag, 0);
        baseMapper.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordReqDTO requestParam, HttpServletRequest request) {
        SecurityUserDetails currentUser = SecurityUtils.getCurrentUserDetails();
        Long currentUserId = currentUser.getUserDO().getId();
        Integer currentUserLevel = currentUser.getRoleLevel();
        String currentUsername = currentUser.getUsername();

        Long targetUserId = Long.parseLong(requestParam.getUserId());
        
        UserDO targetUser = baseMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new ClientException("目标用户不存在");
        }

        Integer targetUserLevel = getTargetUserLevel(targetUser);
        
        if (currentUserLevel == null || targetUserLevel == null) {
            throw new ClientException("无法获取用户权限等级");
        }

        if (currentUserLevel < targetUserLevel) {
            log.warn("权限不足: 操作者 {} (等级 {}) 尝试重置用户 {} (等级 {}) 的密码", 
                currentUsername, currentUserLevel, targetUser.getUsername(), targetUserLevel);
            throw new AccessDeniedException("权限不足：无法重置权限等级更高的用户密码");
        }

        String encodedPassword = passwordEncoder.encode(requestParam.getNewPassword());
        
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getId, targetUserId)
                .set(UserDO::getPassword, encodedPassword);
        
        int updateResult = baseMapper.update(updateWrapper);
        
        if (updateResult == 0) {
            throw new ClientException("密码重置失败");
        }

        String ipAddress = getClientIpAddress(request);
        
        OperationLogDO logDO = OperationLogDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .operatorId(currentUserId)
                .operatorName(currentUsername)
                .operationType("RESET_PASSWORD")
                .operationDesc("重置用户密码")
                .targetUserId(targetUserId)
                .targetUsername(targetUser.getUsername())
                .ipAddress(ipAddress)
                .result(1)
                .build();
        
        operationLogMapper.insert(logDO);
        
        log.info("密码重置成功: 操作者={}, 目标用户={}, IP={}", 
            currentUsername, targetUser.getUsername(), ipAddress);
    }

    private Integer getTargetUserLevel(UserDO user) {
        if (user.getDictId() == null) {
            return 0;
        }
        
        UserPageQueryRespDTO userInfo = baseMapper.getUserWithDictName(user.getId());
        if (userInfo != null && userInfo.getDictName() != null) {
            String dictName = userInfo.getDictName();
            if (dictName.contains("教务处")) {
                return 100;
            } else if (dictName.contains("系教务")) {
                return 50;
            } else if (dictName.contains("系主任")) {
                return 10;
            }
        }
        return 0;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
