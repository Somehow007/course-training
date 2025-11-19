package com.ujs.trainingprogram.tp.utils;

import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.security.SecurityUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Spring Security 工具类
 */
public class SecurityUtils {

    public static SecurityUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
        if (!getCurrentUserDetails().hasPermission(requiredLevel)) {
            throw new AccessDeniedException("当前用户权限等级不足");
        }
    }

    /**
     * 获取当前的用户
     */
    public static UserDO getCurrentUser() {
        return getCurrentUserDetails().getUserDO();
    }

    /**
     * 获取当前用户身份名称（如“教师”）
     */
    public static String getCurrentRoleName() {
        return getCurrentUserDetails().getRoleName();
    }
}
