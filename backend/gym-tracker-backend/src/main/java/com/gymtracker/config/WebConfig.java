package com.gymtracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MemberSessionInterceptor memberSessionInterceptor;

    public WebConfig(MemberSessionInterceptor memberSessionInterceptor) {
        this.memberSessionInterceptor = memberSessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberSessionInterceptor)
                .addPathPatterns("/api/members/**");
    }
}
