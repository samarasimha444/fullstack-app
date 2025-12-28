package com.example.backend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuthSuccessHandler successHandler;

    public SecurityConfig(JwtFilter jwtFilter, OAuthSuccessHandler successHandler) {
        this.jwtFilter = jwtFilter;
        this.successHandler = successHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF (JWT + stateless)
            .csrf(csrf -> csrf.disable())

            // Do not create HTTP sessions
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Disable default login mechanisms
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // Public vs protected routes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",              // home
                    "/oauth2/**",     // OAuth flow
                    "/login/**",      // OAuth callback
                    "/error"          // error page
                ).permitAll()
                .anyRequest().authenticated() // everything else needs auth
            )

            // Google OAuth login configuration
            .oauth2Login(oauth ->
                oauth.successHandler(successHandler)
            )

            // Validate JWT on every request
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // Logout clears cookies
            .logout(logout ->
                logout.deleteCookies("JSESSIONID", "AUTH_TOKEN")
            );

        return http.build();
    }
}
