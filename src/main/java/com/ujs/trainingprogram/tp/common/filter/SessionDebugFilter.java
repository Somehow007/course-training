package com.ujs.trainingprogram.tp.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SessionDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        if (requestURI.startsWith("/api")) {
            HttpSession session = request.getSession(false);
            String sessionId = session != null ? session.getId() : "null";
            String requestedSessionId = request.getRequestedSessionId();
            
            log.debug("请求: {} {}, sessionId={}, requestedSessionId={}", 
                method, requestURI, sessionId, requestedSessionId);
        }
        
        filterChain.doFilter(request, response);
    }
}
