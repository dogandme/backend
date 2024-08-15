package com.mungwithme.security.oauth.handler;


import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.CustomOAuth2User;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.ServletException;
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
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");

        try {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();

            String role = auth.getAuthority();
            String email = customUserDetails.getEmail();

            String accessToken = jwtService.createAccessToken(email, Role.valueOf(role));   // AccessToken 발급
            String refreshToken = jwtService.createRefreshToken();                          // RefreshToken 발급
            
            jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 쿠키에 AccessToken, RefreshToken 담아 응답
            jwtService.updateRefreshToken(email, refreshToken);
        } catch (Exception e) {
            throw e;
        }


    }
}
