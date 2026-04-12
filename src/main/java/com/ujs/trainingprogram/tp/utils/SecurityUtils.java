package com.ujs.trainingprogram.tp.utils;

import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.security.SecurityUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Spring Security 工具类
 */
@Slf4j
public class SecurityUtils {

    public static SecurityUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("当前认证信息: {}, principal类型: {}", 
            auth != null ? auth.getName() : "null", 
            auth != null && auth.getPrincipal() != null ? auth.getPrincipal().getClass().getName() : "null");
        if (auth != null &&
        auth.getPrincipal() instanceof SecurityUserDetails) {
            return ((SecurityUserDetails) auth.getPrincipal());
        }
        throw new AccessDeniedException("用户未登录或会话失效");
    }

    /**
     * 检查用户是否拥有权限
     */
    public static void checkPermissionLevel(int requiredLevel) {
        SecurityUserDetails userDetails = getCurrentUserDetails();
        Integer userRoleLevel = userDetails.getRoleLevel();
        log.info("权限验证 - 用户: {}, 角色等级: {}, 需要等级: {}", 
            userDetails.getUsername(), userRoleLevel, requiredLevel);
        if (!userDetails.hasPermission(requiredLevel)) {
            log.warn("权限验证失败 - 用户: {}, 角色等级: {} < 需要等级: {}", 
                userDetails.getUsername(), userRoleLevel, requiredLevel);
            throw new AccessDeniedException("当前用户权限等级不足");
        }
        log.info("权限验证通过 - 用户: {}", userDetails.getUsername());
    }

    /**
     * 获取当前的用户
     */
    public static UserDO getCurrentUser() {
        return getCurrentUserDetails().getUserDO();
    }

    /**
     * 获取当前用户身份名称（如"教师"）
     */
    public static String getCurrentRoleName() {
        return getCurrentUserDetails().getRoleName();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}
