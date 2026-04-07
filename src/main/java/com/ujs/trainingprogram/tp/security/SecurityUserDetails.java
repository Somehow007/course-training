package com.ujs.trainingprogram.tp.security;

import cn.hutool.core.util.StrUtil;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 封装自定义的 User
 */
@Getter
@AllArgsConstructor
@Slf4j
public class SecurityUserDetails implements UserDetails {

    private final UserDO userDO;

    private final String roleName;

    private final Integer roleLevel;

    /**
     * 获取用户角色权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return userDO.getPassword();
    }

    @Override
    public String getUsername() {
        return userDO.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasPermission(int requiredLevel) {
        if (this.roleLevel == null) {
            log.warn("用户 {} 的角色等级为 null，无法进行权限验证", userDO.getUsername());
            return false;
        }
        boolean result = this.roleLevel >= requiredLevel;
        log.debug("权限比较: roleLevel={}, requiredLevel={}, result={}", this.roleLevel, requiredLevel, result);
        return result;
    }
}
