package com.mungwithme.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommonBaseResult {
    @Schema(description = "응답 코드 번호")
    private int code;

    @Schema(description = "응답 메시지")
    private String message;
}
