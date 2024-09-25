package com.mungwithme.common.exception;


import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class CustomIllegalArgumentException extends IllegalArgumentException {


    // 메세지 매핑시 필요한 값을 담는 배열
    private final Object[] args;

    public CustomIllegalArgumentException(String message) {
        super(message);
        this.args = null;
    }

    public CustomIllegalArgumentException(String message, String... args) {
        super(message);
        this.args = args;
    }

    public CustomIllegalArgumentException(String message, Throwable cause, String... args) {
        super(message, cause);
        this.args = args;
    }

    public CustomIllegalArgumentException(Throwable cause, String... args) {
        super(cause);
        this.args = args;
    }
}
