package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(   // 只允许这些
                        "/login", "/register",
                        "/css/**", "/js/**", "/img/**"
                );
        registry.addInterceptor(new FirstVisitInterceptor()).addPathPatterns("/**");
    }
}

