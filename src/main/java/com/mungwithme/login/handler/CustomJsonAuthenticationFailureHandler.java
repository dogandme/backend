package com.mungwithme.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * JSON 로그인 실패 시 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class CustomJsonAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final BaseResponse baseResponse;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        baseResponse.handleResponse(response, baseResponse.sendErrorResponse(HttpStatus.BAD_REQUEST.value(),
            "아이디, 비밀번호를 확인해주세요"));
    }
}

