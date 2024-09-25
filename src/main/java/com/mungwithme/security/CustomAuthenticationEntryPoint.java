package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final BaseResponse baseResponse;
    private final MessageSource ms;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String code = "error.auth";
        String message;
        try {
            message = ms.getMessage(authException.getMessage(), null, request.getLocale());
        } catch (NoSuchMessageException e) { // 해당 코드에 해당하는 message 가 없다면
            message = ms.getMessage(code, null, request.getLocale());
        }
        baseResponse.handleResponse(response, baseResponse.sendErrorResponse(HttpStatus.UNAUTHORIZED.value(), message));
    }
}
