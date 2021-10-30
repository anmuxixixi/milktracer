package com.amx.milk.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 前端VUe系统的端口号为9527
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:9527","null")
                .allowedMethods("GET","POST","PUT","OPTIONS","DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }

}
