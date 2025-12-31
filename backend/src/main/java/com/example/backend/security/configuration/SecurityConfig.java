package com.example.backend.security.configuration;

import com.example.backend.security.jwt.jwtFilter;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 🔥 enable CORS (uses your CorsConfigurationSource bean)
            .cors(cors -> {})

            // ❌ CSRF not needed for JWT
            .csrf(csrf -> csrf.disable())

            // ❌ No sessions — JWT only
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔐 Google OAuth
            .oauth2Login(oauth ->
                oauth.successHandler(oAuthSuccessHandler)
            )

            // 🔓 Public endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login/**",
                    "/oauth2/**",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 🔐 JWT validation filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
