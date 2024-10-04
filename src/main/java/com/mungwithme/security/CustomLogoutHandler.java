package com.mungwithme.security;

import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.service.UserLogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {


    private final UserLogoutService userLogoutService;
    private final JwtService jwtService;
    private final BaseResponse baseResponse;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // 토큰 검증
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

        if (refreshToken == null || !jwtService.tokenValid(refreshToken)) {
            return;
        }
        // 쿠키 삭제
        jwtService.clearAllCookie(request, response);    // jwt 쿠키 삭제

        // status 로그아웃으로 변경
        userLogoutService.logout(refreshToken, request.getSession().getId());
    }
}
