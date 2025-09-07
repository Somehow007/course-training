package com.ujs.trainingprogram.tp.common.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Map;

/**
 * 拦截器
 * 用于Spring Security登陆验证时额外解析前端JSON格式数据
 */
@Slf4j
public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter { ;
    private final AuthenticationManager authenticationManager;

    public MyAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setAuthenticationManager(authenticationManager);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (!(request instanceof ContentCachingRequestWrapper)) {
            throw new AuthenticationServiceException("请求未正确包装");
        }
        ContentCachingRequestWrapper wrapperRequest = (ContentCachingRequestWrapper) request;

        if (isJsonRequest(wrapperRequest)) {
            try {
                Map<String, String> map = new ObjectMapper().readValue(
                        wrapperRequest.getContentAsByteArray(),
                        Map.class
                );
                String userName = map.get("user_id");
                String password = map.get("user_password");
                log.info("MyAuthenticationFilter");

                if (userName == null) {
                    userName = "";
                }
                if (password == null) {
                    password = "";
                }
                userName = userName.trim();
                log.info("用户名和密码在request中提取成功");
                log.info("user_id: {}  user_password ：{}", userName, password);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userName, password);
                setDetails(request, authenticationToken);
                log.info("authenticationToken 内容: principal={}, credentials={}, details={}",
                        authenticationToken.getPrincipal(),
                        authenticationToken.getCredentials(),
                        authenticationToken.getDetails());

                return super.getAuthenticationManager().authenticate(authenticationToken);
            } catch (IOException e) {
                throw new AuthenticationServiceException("解析请求失败");
            }

        }
        // 非JSON数据用父类原有方式判断
        else {
            return super.attemptAuthentication(request, response);
        }
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        return request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE) ||
                request.getContentType().contains("json/application");
    }
}
