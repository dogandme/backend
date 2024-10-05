package com.mungwithme.common.exception;

/**
 * 토큰이 유효하지 않을 경우 발생
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}