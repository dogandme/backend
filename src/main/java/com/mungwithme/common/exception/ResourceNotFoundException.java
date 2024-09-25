package com.mungwithme.common.exception;


public class ResourceNotFoundException extends RuntimeException {



    // 메세지 매핑시 필요한 값을 담는 배열
    private final Object[] args;


    public ResourceNotFoundException(String message,String... args) {
        super(message);
        this.args = args;
    }
    public ResourceNotFoundException(String message) {
        super(message);
        this.args = null;
    }
}
