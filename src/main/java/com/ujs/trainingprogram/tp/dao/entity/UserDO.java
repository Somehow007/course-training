package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户POJO
 */
@Data
@TableName("user")
public class UserDO implements UserDetails {

    /**
     * 用户Id
     */
    @TableId("user_id")
    private String userId;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 学院编号
     */
    private String collegeId;

    /**
     * 用户状态
     */
    private String userState;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        if (userState.equals("21")) authorities.add(new SimpleGrantedAuthority("admin"));
        else if (userState.equals("20")) {
            authorities.add(new SimpleGrantedAuthority("mainAdmin"));
            authorities.add(new SimpleGrantedAuthority("admin"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userId;
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
}
