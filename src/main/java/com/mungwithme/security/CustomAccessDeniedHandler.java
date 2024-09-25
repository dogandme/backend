package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final BaseResponse baseResponse;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        baseResponse.handleResponse(response, baseResponse.sendErrorResponse(HttpStatus.FORBIDDEN.value(), "리소스 접근 권한 없음"));
    }
}
