package com.mungwithme.security.oauth.dto;

import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.SocialType;
import com.mungwithme.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 소셜별로 받는 데이터를 분기 처리하는 DTO 클래스
 */
@Getter
public class OAuthAttributes {

    private String nameAttributeKey;        // OAuth2 로그인 진행 시 키가 되는 필드 값(PK)
    private OAuth2Response oAuth2Response;  // 소셜 타입별 로그인 유저 정보

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2Response oAuth2Response) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2Response = oAuth2Response;
    }

    /**
     * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * @param socialType 소셜 타
     * @param userNameAttributeName OAuth2 로그인 시 키(PK)가 되는 값
     * @param attributes OAuth 서비스의 유저 정보
     */
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if (socialType == SocialType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2Response(new GoogleResponse(attributes))
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2Response(new NaverResponse(attributes))
                .build();
    }
}
