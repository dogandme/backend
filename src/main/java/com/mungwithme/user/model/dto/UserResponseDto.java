package com.mungwithme.user.model.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private String authorization;   // AccessToken
    private String role;            // 권한
    private String nickname;        // 닉네임
}
