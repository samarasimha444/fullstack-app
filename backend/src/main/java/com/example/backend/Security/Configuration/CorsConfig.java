package com.example.backend.Security.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration // Marks this as Spring configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        // Holds CORS rules sent to the browser
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests ONLY from React frontend
        config.setAllowedOrigins(List.of("http://localhost:5173"));

        // Allow common HTTP methods + preflight
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        // Allow all request headers from frontend
        config.setAllowedHeaders(List.of("*"));

        // REQUIRED when using cookies (JWT in cookies)
        config.setAllowCredentials(true);

        // Apply this CORS config to all endpoints
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Register CORS filter in Spring filter chain
        return new CorsFilter(source);
    }
}
