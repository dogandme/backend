package com.mungwithme.common.exception.handler;


import com.mungwithme.common.exception.CustomIllegalArgumentException;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.exception.UnauthorizedException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.mungwithme"})
@RequiredArgsConstructor
public class GlobalExHandlers {


    private final MessageSource ms;
    private final BaseResponse baseResponse;

    //ExceptionHandler가 붙은 함수는 꼭 protected / private 처리
    //외부에서 함수를 부르게 되면 그대로 에러 객체를 리턴

    /**
     *
     *
     * validation: MethodArgumentNotValidException
     * 400 CustomIllegalArgumentException
     * 404 ResourceNotFoundException
     * 409 DuplicateResourceException
     * 401 CustomAuthenticationEntryPoint.class 로 처리
     * 403 CustomAccessDeniedHandler.class 로 처리
     * 500 Exception.class
     */


    @ExceptionHandler(CustomIllegalArgumentException.class)
    protected ResponseEntity<CommonBaseResult> handleCustomIllegalArgumentException(CustomIllegalArgumentException e,
        HttpServletRequest request) throws IOException {
        String message = getMessage(e.getMessage(), e.getArgs(), request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<CommonBaseResult> handleIllegalArgumentException(IllegalArgumentException e,
        HttpServletRequest request) throws IOException {
        String message = getMessage(e.getMessage(), null, request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    protected ResponseEntity<CommonBaseResult> handleDuplicateResourceException(DuplicateResourceException e,
        HttpServletRequest request) throws IOException {
        String message = getMessage(e.getMessage(), null, request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.CONFLICT.value(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CommonBaseResult> processValidationError(MethodArgumentNotValidException ex,HttpServletRequest request)
        throws IOException {
        return baseResponse.sendErrorResponse(HttpStatus.BAD_REQUEST.value(),
            ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<CommonBaseResult> handleResourceNotFoundException(ResourceNotFoundException e,
        HttpServletRequest request) throws IOException {
        String message = getMessage(e.getMessage(), null, request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.NOT_FOUND.value(), message);
    }


    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<CommonBaseResult> handleUnauthorizedException(UnauthorizedException e,
        HttpServletRequest request) throws IOException {
        String message = getMessage("error.auth", null, request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.UNAUTHORIZED.value(), message);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<CommonBaseResult> handleAuthorizationDeniedException(AuthorizationDeniedException e,
        HttpServletRequest request) throws IOException {
        String message = getMessage("error.forbidden", null, request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.FORBIDDEN.value(), message);
    }



    /**
     * Exception handler
     *
     * @param request
     * @return
     * @throws IOException
     */
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<CommonBaseResult> handleGeneralException(Exception e,
        HttpServletRequest request) throws IOException {
        String message = getMessage("error.internal", null, request.getLocale());
        return baseResponse.sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    /**
     * NoSuchMessageException 공통 처리
     *
     * @param code
     * @param args
     * @param locale
     * @return
     */
    private String getMessage(String code, @Nullable Object[] args, Locale locale) {
        try {
            return ms.getMessage(code, args, locale);
        } catch (NoSuchMessageException ex) {
            return ms.getMessage("error.internal", null, locale);
        }
    }


}
