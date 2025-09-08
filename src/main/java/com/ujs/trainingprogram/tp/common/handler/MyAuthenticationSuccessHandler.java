package com.ujs.trainingprogram.tp.common.handler;

import com.alibaba.fastjson2.JSONObject;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component("myAuthenticationSuccessHandler")
@Slf4j
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        String username = authentication.getName();
        log.info("认证成功 | SessionID={} | 用户={} | 角色={}",
                session != null ? session.getId() : "无会话",
                username,
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDO userDO = (UserDO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(JSONObject.toJSONString(ResultMessage.LOGIN_SUCCESS));
        }
        log.info("Principal类型: {}", authentication.getPrincipal().getClass().getName());
        log.warn("MyAuthenticationSuccessHandler用户信息：{}", userDO);
    }
}
