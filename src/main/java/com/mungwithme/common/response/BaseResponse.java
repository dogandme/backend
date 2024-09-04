package com.mungwithme.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Component
public class BaseResponse {

    private final MessageSource messageSource;

    public <T> CommonResult<T> getContentResult(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setContent(data);
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public CommonBaseResult getSuccessResult() {
        CommonBaseResult result = new CommonBaseResult();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public CommonBaseResult getFailResult(int code, String message) {
        CommonBaseResult result = new CommonBaseResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 정상 응답
     */
    public void sendSuccessResponse(HttpServletResponse httpResponse, int statusCode, ObjectMapper objectMapper) throws IOException {
        CommonBaseResult result = getSuccessResult();
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(statusCode);

        PrintWriter writer = httpResponse.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
    }

    /**
     * 정상 JSON 응답
     */
    public void sendContentResponse(Object data, HttpServletResponse httpResponse, int statusCode, ObjectMapper objectMapper) throws IOException {
        CommonBaseResult result = getContentResult(data);
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(statusCode);

        PrintWriter writer = httpResponse.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
    }

    /**
     * 에러 응답
     */
    public void sendErrorResponse(HttpServletResponse httpResponse, int statusCode, String message, ObjectMapper objectMapper) throws IOException {
        CommonBaseResult result = getFailResult(statusCode, message);
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(statusCode);

        PrintWriter writer = httpResponse.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
    }
}
