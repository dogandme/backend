package com.mungwithme.common.response;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BaseResponse {

    private final MessageSource messageSource;

    public <T> CommonResult<T> getContentResult(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setContent(data);
        result.setCode(0);
        result.setMessage("success");
        return result;
    }

    public CommonBaseResult getSuccessResult() {
        CommonBaseResult result = new CommonBaseResult();
        result.setCode(0);
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
}
