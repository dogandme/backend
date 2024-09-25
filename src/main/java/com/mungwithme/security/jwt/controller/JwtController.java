package com.mungwithme.security.jwt.controller;

import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 *질문) 비회원도 접근 가능?
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class JwtController {

    private final JwtService jwtService;
    private final BaseResponse baseResponse;

    @GetMapping("")
    public ResponseEntity<CommonBaseResult> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserResponseDto userResponseDto = new UserResponseDto();
        String accessToken = "";

        String refreshToken = jwtService.extractRefreshToken(request)
                    .filter(jwtService::isTokenValid)   // refresh Token이 있고 검증되면 반환
                    .orElse(null);                // 없으면 null 반환

        if (refreshToken != null) {

            // Refresh Token 으로 유저 정보 찾기 & Access/Refresh Token 재발급 메소드
            accessToken = jwtService.checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            userResponseDto.setAuthorization(accessToken);

            return baseResponse.sendContentResponse(userResponseDto, 200);
        } else {
            return baseResponse.sendErrorResponse(401, "RefreshToken 검증에 실패했습니다.");
        }
    }


}
