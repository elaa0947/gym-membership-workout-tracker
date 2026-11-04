package com.gymtracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.equals("/api/members/register")
                || path.equals("/api/members/login")
                || path.equals("/api/members/logout")) {
            return true;
        }

        var session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("memberId");
        Long sessionMemberId = value instanceof Long id ? id : null;
        String[] segments = path.split("/");
        String memberId = "";
        for (int i = 3; i < segments.length; i++) {
            if (segments[i].matches("\\d+")) {
                memberId = segments[i];
                break;
            }
        }
        if (sessionMemberId == null || !String.valueOf(sessionMemberId).equals(memberId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Please sign in to access this account.\"}");
            return false;
        }
        return true;
    }
}
