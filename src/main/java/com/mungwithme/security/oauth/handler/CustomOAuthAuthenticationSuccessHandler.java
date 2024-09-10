package com.mungwithme.security.oauth.handler;


import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.PrincipalDetails;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * oauth2로그인 성공시 토큰발급 및 쿠키전달하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {

            // 1. PrincipalDetails에서 email 가져오기
            PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            // 2. PrincipalDetails에서 권한 가져오기
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)  // "ROLE_USER" 등의 문자열로 변환
                    .toList();
            String role = roles.get(0);

            // 3. 토큰 발급 및 저장
            String accessToken = jwtService.createAccessToken(email, role);
            String refreshToken = jwtService.createRefreshToken();
            jwtService.setRefreshTokenCookie(response, refreshToken); // 응답 쿠키에 RefreshToken 담아 응답
            jwtService.updateRefreshToken(email, refreshToken);

            // 4. 기존 회원일 경우 닉네임 쿠키에 저장
            int maxAge = 3600000;
            userRepository.findByRefreshToken(refreshToken)
                    .ifPresent(user -> {
                        Cookie nicknameCookie = new Cookie("nickname", user.getNickname());
                        nicknameCookie.setPath("/login");
                        nicknameCookie.setMaxAge(maxAge);
                        response.addCookie(nicknameCookie);
                    });

            // accessToken과 권한 쿠키에 저장
            Cookie authorizationCookie = new Cookie("authorization", accessToken);
            Cookie roleCookie = new Cookie("role", role);

            authorizationCookie.setPath("/login");
            roleCookie.setPath("/login");

            authorizationCookie.setMaxAge(maxAge);
            roleCookie.setMaxAge(maxAge);

            response.addCookie(authorizationCookie);
            response.addCookie(roleCookie);

            response.sendRedirect("http://localhost:5173/login");
        } catch (Exception e) {
            throw e;
        }


    }
}
