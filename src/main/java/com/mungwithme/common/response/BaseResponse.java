package com.mungwithme.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Component
public class BaseResponse {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

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

    public CommonBaseResult getSuccessResult(String message) {
        CommonBaseResult result = new CommonBaseResult();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }


    public CommonBaseResult getFailResult(int code, String message) {
        CommonBaseResult result = new CommonBaseResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 정상 응답
     */
    public ResponseEntity<CommonBaseResult> sendSuccessResponse(int statusCode, String message) throws IOException {
        CommonBaseResult result = getSuccessResult(message);

        return ResponseEntity
            .status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result);
    }

    /**
     * 정상 응답
     */
    public ResponseEntity<CommonBaseResult> sendSuccessResponse(int statusCode) throws IOException {
        CommonBaseResult result = getSuccessResult();

        return ResponseEntity
            .status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result);
    }

    /**
     * 정상 JSON 응답
     */
    public ResponseEntity<CommonBaseResult> sendContentResponse(Object data, int statusCode) throws IOException {
        CommonBaseResult result = getContentResult(data);

        return ResponseEntity
            .status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result);
    }

    /**
     * 에러 응답
     */
    public ResponseEntity<CommonBaseResult> sendErrorResponse(int statusCode, String message) throws IOException {
        CommonBaseResult result = getFailResult(statusCode, message);

        return ResponseEntity
            .status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(result);
    }

    /**
     * ResponseEntity 자체를 return 못할 경우 이용
     */
    public void handleResponse(HttpServletResponse response, ResponseEntity<?> result) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(result.getStatusCode().value());

        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(result.getBody()));
        writer.flush();
    }
}
