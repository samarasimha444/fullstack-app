package com.example.backend.security.configuration;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.jwt.JwtUtil;
import com.example.backend.service.MailService;
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
    private final JwtUtil jwtUtil;
    private final MailService mailService; // ✅ ADDED

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String sub = oauthUser.getAttribute("sub");
        if (sub == null) {
            throw new IllegalStateException("Google sub not found");
        }

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        User user = userRepository.findById(sub)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .id(sub)
                                .email(email)
                                .name(name)
                                .picture(picture)
                                .build()
                ));

        // ✅ SEND LOGIN SUCCESS EMAIL (ASYNC)
        if (email != null) {
            mailService.sendLoginSuccessMail(email);
        }

        String jwt = jwtUtil.generateToken(user.getId());

        String cookie =
                "JWT=" + jwt +
                "; Path=/" +
                "; HttpOnly" +
                (request.isSecure() ? "; Secure" : "") +
                "; SameSite=None";

        response.setHeader("Set-Cookie", cookie);

        response.sendRedirect("https://localhost:5173/");
    }
}
