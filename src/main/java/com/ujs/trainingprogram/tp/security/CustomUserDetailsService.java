package com.ujs.trainingprogram.tp.security;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ujs.trainingprogram.tp.common.exception.ServiceException;
import com.ujs.trainingprogram.tp.dao.entity.SysDictDO;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dao.mapper.SysDictMapper;
import com.ujs.trainingprogram.tp.dao.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Spring Security 加载用户信息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    private final SysDictMapper sysDictMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("加载用户信息: {}", username);
        UserDO userDO = userMapper.selectOne(Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0));
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException("用户不存在或已被删除: " + username);
        }

        Long dictId = userDO.getDictId();
        if (Objects.isNull(dictId)) {
            throw new ServiceException("用户权限配置为空，username: " + username);
        }

        SysDictDO sysDictDO = sysDictMapper.selectOne(Wrappers.lambdaQuery(SysDictDO.class)
                .eq(SysDictDO::getId, dictId)
                .eq(SysDictDO::getDelFlag, 0));
        if (Objects.isNull(sysDictDO)) {
            throw new ServiceException("查询用户权限失败， dictId: " + dictId);
        }

        Integer roleLevel = sysDictDO.getSortOrder();
        String roleName = sysDictDO.getDictName();
        
        log.info("用户 {} 权限信息: dictId={}, dictName={}, dictCode={}, sortOrder={}", 
            username, dictId, sysDictDO.getDictName(), sysDictDO.getDictCode(), roleLevel);
        
        if (roleLevel == null) {
            log.warn("用户 {} 的角色等级(sortOrder)为 null，将使用默认值 0", username);
            roleLevel = 0;
        }

        return new SecurityUserDetails(userDO, roleName, roleLevel);
    }
}
