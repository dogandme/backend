package com.mungwithme.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * JSON 로그인 실패 시 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class CustomJsonAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BaseResponse baseResponse;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        baseResponse.sendErrorResponse(response, 401, "로그인 실패", objectMapper);
        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
}

