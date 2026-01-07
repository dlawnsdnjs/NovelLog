package com.example.novelcharacter.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스만 처리
        registry.addResourceHandler(
                        "/static/**",
                        "/*.js",
                        "/*.css",
                        "/*.png",
                        "/*.svg",
                        "/*.json",
                        "/*.ico"
                )
                .addResourceLocations("classpath:/static/");
    }
}