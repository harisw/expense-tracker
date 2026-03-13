package com.harisw.springexpensetracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${client.origins}")
    private String[] clientOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")
                .allowedOrigins(clientOrigins)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
