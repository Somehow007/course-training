package com.ujs.trainingprogram.tp.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujs.trainingprogram.tp.common.exception.ValidateCodeException;
import com.ujs.trainingprogram.tp.common.handler.MyAuthenticationFailuerHandler;
import com.ujs.trainingprogram.tp.controller.ValidateCodeController;
import com.ujs.trainingprogram.tp.model.ImageCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 验证码过滤器
 * 验证前端登录请求验证码参数imageCode
 */
@Slf4j
public class ValidateCodeFilter extends OncePerRequestFilter {

    private MyAuthenticationFailuerHandler myAuthenticationFailuerHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isLoginRequest(request)) {

            log.info(("进入 ValidateCodeFilter，URI={}, Method={}"), request.getRequestURI(), request.getMethod());
            ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
            // 强制读取请求体以触发缓存
            try {
                wrapperRequest.getInputStream().readAllBytes();
            } catch (IOException e) {
                log.error("读取请求体失败", e);
            }

            try {
                validate(wrapperRequest);
            } catch (ValidateCodeException e) {
                myAuthenticationFailuerHandler.onAuthenticationFailure(wrapperRequest, response, e);
                return;
            }
            filterChain.doFilter(wrapperRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }

    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return StringUtils.equals("/loginByAccount", request.getRequestURI()) &&
                StringUtils.endsWithIgnoreCase(request.getMethod(), "POST");
    }

    private void validate(ContentCachingRequestWrapper wrapperRequest) throws ValidateCodeException {
        HttpSession session = wrapperRequest.getSession();
        log.info("开始验证码验证，SessionID={}", session.getId());
        String codeInRequest = parseJsonRequestForImageCode(wrapperRequest);

        log.info("开始验证码验证，1");
        ImageCode codeInSession = (ImageCode) session.getAttribute(ValidateCodeController.SESSION_KEY);
        if (codeInSession == null) {
            log.error("验证码不存在，SessionID={}", session.getId());
            throw new ValidateCodeException("验证码不存在或已过期");
        }
        log.info("开始验证码验证，2");
        log.info("验证码会话值：{}", codeInSession.getCode() != null ? codeInSession.getCode() : "null");
        log.info("请求参数值：{}", codeInRequest);
        if (StringUtils.isEmpty(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }
        if (codeInSession == null) {
            log.info("验证码不存在");
            throw new ValidateCodeException("验证码不存在");
        }
        if (codeInSession.isExpired()) {
            wrapperRequest.getSession().removeAttribute(ValidateCodeController.SESSION_KEY);
            log.info("验证码已过期");
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            log.info("验证码不匹配");
            throw new ValidateCodeException("验证码不匹配");
        }
        wrapperRequest.getSession().removeAttribute(ValidateCodeController.SESSION_KEY);
    }

    private String parseJsonRequestForImageCode(ContentCachingRequestWrapper wrapperRequest) {
        try {
            log.info("解析验证码请求，Content-Type={}", wrapperRequest.getContentType());
            byte[] contentBytes = wrapperRequest.getContentAsByteArray();

            // 检查请求体是否为空
            if (contentBytes == null || contentBytes.length == 0) {
                log.error("请求体为空");
                throw new ValidateCodeException("请求体不能为空");
            }

            String rawBody = new String(contentBytes, StandardCharsets.UTF_8);
            log.info("请求体原始数据：{}", rawBody);

            if (wrapperRequest.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                Map<String, String> map = new ObjectMapper().readValue(contentBytes, Map.class);
                String code = map.get("imageCode");
                log.info("解析后的用户Id: {}", map.get("user_id"));
                log.info("解析后的密码: {}", map.get("user_password"));
                log.info("解析后的验证码: {}", code);
                return code;
            }
            return wrapperRequest.getParameter("imageCode");
        } catch (IOException e) {
            log.error("解析请求体失败", e);
            throw new ValidateCodeException("解析请求体失败: " + e.getMessage());
        }
    }

    public MyAuthenticationFailuerHandler getMyAuthenticationFailuerHandler() {
        return myAuthenticationFailuerHandler;
    }

    public void setMyAuthenticationFailuerHandler(MyAuthenticationFailuerHandler myAuthenticationFailuerHandler) {
        this.myAuthenticationFailuerHandler = myAuthenticationFailuerHandler;
    }
}
