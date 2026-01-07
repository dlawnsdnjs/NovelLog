package com.example.novelcharacter.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .exposedHeaders("Set-Cookie", "Authorization", "Access", "Refresh")
                .allowedOrigins("http://localhost:3000/", "ec2-3-39-42-1.ap-northeast-2.compute.amazonaws.com:8080", "https://d2dan5t0q4jwaf.cloudfront.net")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
