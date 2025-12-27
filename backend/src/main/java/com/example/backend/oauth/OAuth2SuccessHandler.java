package com.example.backend.oauth;

import com.example.backend.Entity.UserEntity;
import com.example.backend.Repository.UserRepository;
import com.example.backend.Security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // create user if not exists
        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity u = new UserEntity();
                    u.setEmail(email);
                    u.setUsername(name);
                    u.setProvider("GOOGLE");
                    return userRepository.save(u);
                });

        // generate YOUR jwt
        String jwt = jwtUtil.generateToken(user.getUsername());

        // redirect to frontend with jwt
        response.sendRedirect(
                "http://localhost:3000/oauth-success?token=" + jwt
        );
    }
}
