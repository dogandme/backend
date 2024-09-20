package com.mungwithme.security.oauth.handler;

import com.mungwithme.common.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 소셜 로그인 실패 시 호출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuthAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final BaseResponse baseResponse;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        baseResponse.handleResponse(response, baseResponse.sendErrorResponse(500, "예상치 못한 문제가 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }
}
