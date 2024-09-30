package com.mungwithme.security.oauth.handler;


import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.PrincipalDetails;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * oauth2로그인 성공시 토큰발급 및 쿠키전달하는 핸들러
 */

@Slf4j
public class CustomOAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomOAuthAuthenticationSuccessHandler(JwtService jwtService, UserService userService,
        OAuth2AuthorizedClientService authorizedClientService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

            // oAuth에서 제공해주는 refreshToken 기한 1년

            String oAuthRefreshToken = null;
            if (authorizedClient.getRefreshToken() != null) {
                oAuthRefreshToken = authorizedClient.getRefreshToken().getTokenValue();
            }

            // oAuth에서 제공해주는 accessToken 기한 1 시간
            String oAuthAccessToken = authorizedClient.getAccessToken().getTokenValue();

            // 1. PrincipalDetails에서 email 가져오기
            PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();

            String email = oAuth2User.getUsername();

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

            String finalOAuthRefreshToken = oAuthRefreshToken;

            // 4. 기존 회원일 경우 닉네임 쿠키에 저장
            int maxAge = 3600000;

            // oAuthApi 에서 제공 하는 refreshToken,AccessToken 저장
            // 로그인 성공을 했을때
            // 닉네임을 리턴을 하는데
            // 기존회원인지 신규 가입인지
            // Role NONE -> 닉네임 X
            userService.editRefreshTokenAndSetCookie(email, refreshToken, oAuthAccessToken, finalOAuthRefreshToken,
                response, maxAge);


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
