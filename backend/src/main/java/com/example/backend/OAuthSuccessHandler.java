package com.example.backend;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;


@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuthSuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        System.err.println("OAuth User Attributes: " + oauthUser.getAttributes());

        String email = oauthUser.getAttribute("email");
        String name  = oauthUser.getAttribute("name");
        String sub   = oauthUser.getAttribute("sub");
        String picture = oauthUser.getAttribute("picture");

        User user = userRepository
                .findByProviderAndProviderId("GOOGLE", sub)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setProvider("GOOGLE");
                    u.setProviderId(sub);
                    u.setPicture(picture);
                    u.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(u);
                });

        // 🔐 CREATE JWT
        String jwt = jwtUtil.generateToken(user.getId());

        // 🍪 SET JWT IN HTTP-ONLY COOKIE
        Cookie cookie = new Cookie("AUTH_TOKEN", jwt);
        cookie.setHttpOnly(true);      // JS cannot read
        cookie.setSecure(false);       // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 1 day
        cookie.setAttribute("SameSite", "Lax");


        response.addCookie(cookie);

        // ✅ REDIRECT TO FRONTEND DASHBOARD
        response.sendRedirect("http://localhost:5173/dashboard");
    }
}
