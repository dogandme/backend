package com.mungwithme.security.oauth.handler;


import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.CustomOAuth2User;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;


import java.util.Iterator;

/**
 * oauth2로그인 성공시 토큰발급 및 쿠키전달하는 핸들러
 */


@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        String email = customUserDetails.getEmail();


        String accessToken = jwtService.createAccessToken(email, Role.valueOf(role));   // AccessToken 발급


        String refreshToken = jwtService.createRefreshToken();      // RefreshToken 발급

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);  // sendAccessAndRefreshToken에서는 발급만 함으로 refresh Token 저장 필요
                });


        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 쿠키에 AccessToken, RefreshToken 담아 응답


    }
}
