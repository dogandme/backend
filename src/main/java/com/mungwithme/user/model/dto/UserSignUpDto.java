package com.mungwithme.user.model.dto;

import com.mungwithme.user.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 일반 회원가입 요청 DTO
 */
@NoArgsConstructor
@Setter
@Getter
public class UserSignUpDto {
    // 1차
    private String email;
    private String password;

    // 2차
    private Long userId;
    private String nickname;
    private Gender gender;
    private int age;
    private String region;
    private Boolean marketingYn;
}
