package com.mungwithme.security.oauth.dto;

import com.mungwithme.common.util.TokenUtils;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.SocialType;
import com.mungwithme.user.model.entity.User;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 소셜별로 받는 데이터를 분기 처리하는 DTO 클래스
 */
@Slf4j
@Builder
public record OAuth2UserInfo(
    String name,
    String email,
    String profile,
    SocialType socialType
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        log.info("attributes : {}", attributes);
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        };
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .email((String) ((Map<String, Object>) attributes.get("response")).get("email"))
            .socialType(SocialType.NAVER)
            .build();
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
            .email((String) attributes.get("email"))
            .socialType(SocialType.GOOGLE)
            .build();
    }

    public User toEntity() {
        return User.builder()
            .email(email)
            .role(Role.NONE)
            .token(TokenUtils.getToken())
            .socialType(socialType)
            .build();
    }
}
