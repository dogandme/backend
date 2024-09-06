package com.mungwithme.security.oauth.handler;


import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.CustomOAuth2User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * oauth2로그인 성공시 토큰발급 및 쿠키전달하는 핸들러
 * 
 * @modification.author 장수현
 * @modification.date 2024.8.12
 * @modification.details jwtService를 이용하여 코드 간소화
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();

            String role = auth.getAuthority();
            String email = customUserDetails.getEmail();

            String accessToken = jwtService.createAccessToken(email, role);   // AccessToken 발급
            String refreshToken = jwtService.createRefreshToken();            // RefreshToken 발급

            // Todo 일반 로그인 로직과 통일되게 통합 메소드 찾기
            jwtService.setRefreshTokenCookie(response, refreshToken); // 응답 쿠키에 RefreshToken 담아 응답
            jwtService.updateRefreshToken(email, refreshToken);

            // 기존 회원일 경우 닉네임 쿠키에 저장
            int maxAge = 3600000;
            userRepository.findByRefreshToken(refreshToken)
                    .ifPresent(user -> {
                        Cookie nicknameCookie = new Cookie("nickname", user.getNickname());
                        nicknameCookie.setPath("/login");
                        nicknameCookie.setMaxAge(maxAge);
                        response.addCookie(nicknameCookie);
                    });

            // 각 값을 쿠키로 설정
            Cookie authorizationCookie = new Cookie("authorization", accessToken);
            Cookie roleCookie = new Cookie("role", role);

            // 쿠키 속성 설정 (예: 경로, 유효기간 등)
            authorizationCookie.setPath("/login");
            roleCookie.setPath("/login");

            // 유효기간 설정 (예: 1시간)
            authorizationCookie.setMaxAge(maxAge);
            roleCookie.setMaxAge(maxAge);

            // 쿠키를 응답에 추가
            response.addCookie(authorizationCookie);
            response.addCookie(roleCookie);

            response.sendRedirect("http://localhost:5173/login");

//            response.getWriter().write(jsonResponse);
//            response.getWriter().flush(); // 응답을 클라이언트에 전송
        } catch (Exception e) {
            throw e;
        }


    }
}
