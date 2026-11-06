package com.gymtracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MemberSessionInterceptor memberSessionInterceptor;
    private final AdminSessionInterceptor adminSessionInterceptor;

    public WebConfig(
            MemberSessionInterceptor memberSessionInterceptor,
            AdminSessionInterceptor adminSessionInterceptor) {
        this.memberSessionInterceptor = memberSessionInterceptor;
        this.adminSessionInterceptor = adminSessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSessionInterceptor)
                .addPathPatterns("/api/members/**");
        registry.addInterceptor(adminSessionInterceptor)
                .addPathPatterns("/api/admin/**");
    }
}
