package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler  implements LogoutHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BaseResponse baseResponse;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 토큰 검증
        Optional<String> refreshToken = jwtService.extractRefreshToken(request);
        if (refreshToken.isEmpty() || !jwtService.isTokenValid(refreshToken.get())) {
            try {
                baseResponse.handleResponse(httpResponse, baseResponse.sendErrorResponse(401, "토큰 검증에 실패했습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // refreshToken으로 User 조회
        Optional<User> byRefreshToken = userRepository.findByRefreshToken(refreshToken.get());
        if (byRefreshToken.isEmpty()) {
            try {
                baseResponse.handleResponse(response, baseResponse.sendErrorResponse(404, "회원을 찾을 수 없습니다."));   // 조회된 User가 없을 경우 에러
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        //로그아웃 진행
        jwtService.clearAllCookie(request,response);    // jwt 쿠키 삭제
        jwtService.updateRefreshToken(byRefreshToken.get().getEmail(), null); // Refresh 토큰 DB에서 제거
    }
}
