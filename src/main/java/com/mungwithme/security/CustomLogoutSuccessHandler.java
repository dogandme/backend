package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final BaseResponse baseResponse;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        baseResponse.handleResponse(response, baseResponse.sendSuccessResponse(200));
    }
}
