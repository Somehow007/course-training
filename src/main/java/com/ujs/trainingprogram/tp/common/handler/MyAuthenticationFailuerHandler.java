package com.ujs.trainingprogram.tp.common.handler;

import com.alibaba.fastjson2.JSONObject;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component("myAuthenticationFailuerHandler")
public class MyAuthenticationFailuerHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write(JSONObject.toJSONString(ResultMessage.LOGIN_ERROR_CODE));
        }
    }
}
