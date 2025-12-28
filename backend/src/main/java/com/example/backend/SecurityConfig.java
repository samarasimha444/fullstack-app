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
          .csrf(csrf -> csrf.disable())

          // 🔥 FORCE STATELESS
          .sessionManagement(session ->
              session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )

          // 🔥 DISABLE DEFAULT FORM LOGIN + HTTP BASIC
          .formLogin(form -> form.disable())
          .httpBasic(basic -> basic.disable())

          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/", 
                  "/oauth2/**",
                  "/login/**",
                  "/error"
              ).permitAll()
              .anyRequest().authenticated()
          )

          // OAuth only for login
          .oauth2Login(oauth ->
              oauth.successHandler(successHandler)
          )

          // 🔥 JWT AUTH FOR ALL REQUESTS
          .addFilterBefore(
              jwtFilter,
              UsernamePasswordAuthenticationFilter.class
          )

          // 🔥 CLEAN SESSION COOKIE
          .logout(logout ->
              logout.deleteCookies("JSESSIONID", "AUTH_TOKEN")
          );

        return http.build();
    }
}
