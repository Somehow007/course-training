package com.ujs.trainingprogram.tp.controller;

import com.ujs.trainingprogram.tp.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        log.info("测试接口被调用成功");
        HttpSession session = request.getSession(false);
        log.info("测试接口访问 | SessionID={} | 客户端IP={}",
                session != null ? session.getId() : "无有效会话",
                request.getRemoteAddr()
        );
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            log.warn("TestController用户信息：{}", user);
        } else {
            log.error("Principal类型异常: {}", authentication.getPrincipal().getClass());
        }


        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                System.out.println("当前用户名称：" + user.getUserId());
                return "认证用户: " + user.getUserId();
            }
            System.out.println("匿名用户：" + principal);
            return "匿名用户";
        }

        return "Hello World";
    }
}
