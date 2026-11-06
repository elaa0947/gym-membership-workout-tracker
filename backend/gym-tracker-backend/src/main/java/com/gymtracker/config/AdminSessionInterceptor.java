package com.gymtracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        if (path.equals("/api/admin/login") || path.equals("/api/admin/logout")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object role = session == null ? null : session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Admin access required. Please sign in as an administrator.\"}");
            return false;
        }
        return true;
    }
}
