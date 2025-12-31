package com.example.backend.security.configuration;

import com.example.backend.Repository.UserRepository;
import com.example.backend.entity.User;
import com.example.backend.security.jwt.jwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final jwtUtil JwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // 🔐 Google-authenticated user
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // 🔥 UNIQUE & STABLE identifier
        String sub = oauthUser.getAttribute("sub");
        if (sub == null) {
            throw new IllegalStateException("Google sub not found");
        }

        // Optional profile fields
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        // ✅ Load or create user using sub as ID
        User user = userRepository.findById(sub)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .id(sub)        // 👈 sub stored as primary key
                                .email(email)
                                .name(name)
                                .picture(picture)
                                .build()
                ));

        // 🔐 JWT payload = sub
        String jwt = JwtUtil.generateToken(user.getId());

        // 🍪 HttpOnly cookie
        Cookie cookie = new Cookie("JWT", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // set true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day

        response.addCookie(cookie);

        // 🔁 Redirect to frontend
        response.sendRedirect("http://localhost:5173/dashboard");
    }
}
