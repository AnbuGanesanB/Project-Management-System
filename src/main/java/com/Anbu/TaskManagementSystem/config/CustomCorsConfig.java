package com.Anbu.TaskManagementSystem.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Component
public class CustomCorsConfig implements CorsConfigurationSource {

    @Value("${frontend.allowed-origin}")
    private String allowedFrontEndPort;

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        String origin = request.getHeader("Origin");
        String userAgent = request.getHeader("User-Agent");

        if (origin != null && origin.equals(allowedFrontEndPort)) {
            corsConfiguration.setAllowedOrigins(List.of(allowedFrontEndPort));
        }else if (origin != null) {
            System.out.println("Request is coming from "+origin+" instead of Authorised origin " + allowedFrontEndPort);
            throw new RuntimeException("CORS violation: Request from unauthorized origin: " + origin);
        }
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }
}
