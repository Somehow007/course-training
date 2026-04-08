package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.authentication.RequireAuthentication;
import com.ujs.trainingprogram.tp.common.constant.AuthConstant;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import com.ujs.trainingprogram.tp.dto.req.user.UserLoginReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.user.UserRegistryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.user.UserPageQueryRespDTO;
import com.ujs.trainingprogram.tp.security.SecurityUserDetails;
import com.ujs.trainingprogram.tp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "用户请求管理")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "分页查询用户数据")
    @GetMapping("/api/user/mainAdmin/page")
    public Result<IPage<UserPageQueryRespDTO>> pageQueryUser(UserPageQueryReqDTO requestParam) {
        return Results.success(userService.pageQueryUser(requestParam));
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "用户注册")
    @PostMapping("/api/user/mainAdmin/registry")
    public Result<Void> registryUser(@RequestBody UserRegistryReqDTO requestParam) {
        userService.registryUser(requestParam);
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "删除用户")
    @DeleteMapping("/api/user/mainAdmin/delete/{id}")
    public Result<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(Long.parseLong(id));
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "启用用户")
    @PutMapping("/api/user/mainAdmin/enable/{id}")
    public Result<Void> enableUser(@PathVariable String id) {
        userService.enableUser(Long.parseLong(id));
        return Results.success();
    }

    @Operation(summary = "用户登陆")
    @PostMapping("/api/login")
    public Result<?> login(@RequestBody @Valid UserLoginReqDTO requestParam, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    requestParam.getUsername(),
                    requestParam.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
            UserDO userDO = userDetails.getUserDO();
            Integer roleLevel = userDetails.getRoleLevel();
            
            String sessionId = request.getSession().getId();
            log.info("用户登录成功: username={}, roleLevel={}, roleName={}, sessionId={}", 
                userDO.getUsername(), roleLevel, userDetails.getRoleName(), sessionId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", userDO.getId());
            data.put("username", userDO.getUsername());
            data.put("identity", roleLevel != null ? String.valueOf(roleLevel) : "0");
            return Results.success(data);
        } catch (BadCredentialsException ex) {
            throw new ClientException("用户名或密码错误");
        }
    }

    @Operation(summary = "检查会话状态")
    @GetMapping("/api/check-session")
    public Result<?> checkSession(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
            UserDO userDO = userDetails.getUserDO();
            Integer roleLevel = userDetails.getRoleLevel();
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", userDO.getId());
            data.put("username", userDO.getUsername());
            data.put("identity", roleLevel != null ? String.valueOf(roleLevel) : "0");
            return Results.success(data);
        }
        
        return Results.success(null);
    }
}
