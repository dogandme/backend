package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final BaseResponse baseResponse;
    private final MessageSource ms;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {


        String code = "error.forbidden";
        String message;
        try {
            message = ms.getMessage(accessDeniedException.getMessage(), null, request.getLocale());
        } catch (NoSuchMessageException e) { // 해당 코드에 해당하는 message 가 없다면
            message = ms.getMessage(code, null, request.getLocale());
        }
        baseResponse.handleResponse(response, baseResponse.sendErrorResponse(HttpStatus.FORBIDDEN.value(), message));
    }
}
