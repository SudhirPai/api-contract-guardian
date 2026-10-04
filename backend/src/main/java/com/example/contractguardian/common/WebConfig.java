package com.example.contractguardian.common;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/api/**").allowedOrigins("http://localhost:5173").allowedMethods("*");
    }
}