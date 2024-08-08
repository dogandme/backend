package com.mungwithme.user.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일반 회원가입 요청 DTO
 */
@NoArgsConstructor
@Getter
public class UserSignUpDto {
    private String email;
    private String password;
    private String nickname;
    private int age;
    private String city;
}
