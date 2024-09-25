package com.mungwithme.common.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailAuthRequestDto {

    @Email
    @NotEmpty(message = "{error.NotEmpty.email}")
    private String email;

    @NotEmpty(message = "{error.NotEmpty.authCode}")
    private String authNum;
}
