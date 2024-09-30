package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserLogoutService;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler  implements LogoutHandler {


    private final UserLogoutService userLogoutService;
    private final JwtService jwtService;
    private final BaseResponse baseResponse;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // 토큰 검증
        String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            try {
                baseResponse.handleResponse(response, baseResponse.sendErrorResponse(401, "토큰 검증에 실패했습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }



        //로그아웃 진행
        jwtService.clearAllCookie(request,response);    // jwt 쿠키 삭제

        //
        userLogoutService.logout(refreshToken);
    }
}
