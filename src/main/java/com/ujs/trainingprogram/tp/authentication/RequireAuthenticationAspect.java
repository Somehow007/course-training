package com.ujs.trainingprogram.tp.authentication;

import com.ujs.trainingprogram.tp.utils.SecurityUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 鉴权用户权限切面控制器
 */
@Aspect
@Component
public class RequireAuthenticationAspect {

    @Before("@annotation(require)")
    public void checkPermission(RequireAuthentication require) {
        SecurityUtils.checkPermissionLevel(Integer.parseInt(require.value()));
    }
}
