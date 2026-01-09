package com.example.backend.security.configuration;

import com.example.backend.filters.RateLimitFilter;
import com.example.backend.filters.jwtFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final jwtFilter jwtFilter;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .oauth2Login(oauth ->
                oauth.successHandler(oAuthSuccessHandler)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/ws/**",
                    "/login/**",
                    "/oauth2/**",
                    "/error",
                    "/testing",
                    "/presence/**",
                    "/actuator/**",
                    "/topic/**",
                    "/app/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 🚦 Rate limiting FIRST
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)

            // 🔐 JWT validation AFTER rate limiting
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
