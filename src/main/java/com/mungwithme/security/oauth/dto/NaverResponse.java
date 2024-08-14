package com.mungwithme.security.oauth.dto;

import java.util.Map;

/**
 * 네이버는 값이 response에 담아오기 때문에 한번 값을 필터링해준다.
 * @modification.author 장수현
 * @modification.date 2024.8.12
 * @modification.details interface -> abstract class
 */
public class NaverResponse extends OAuth2Response {

    public NaverResponse(Map<String, Object> attributes) {
        super(attributes);
    }

    // 소셜로그인업체
    @Override
    public String getProvider() {
        return "naver";
    }

    // 각 소셜로그인쪽에서 제공해주는 id
    @Override
    public String getProviderId() {
        return (String) getResponseAttribute("id");
    }

    // 이메일
    @Override
    public String getEmail() {
        return (String) getResponseAttribute("email");
    }

    // 실명
    @Override
    public String getName() {
        return (String) getResponseAttribute("name");
    }

    @Override
    public String getImageUrl() {
        return (String) getResponseAttribute("profile_image");
    }

    // response 필드를 가져오는 공통 메서드
    private Object getResponseAttribute(String key) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            return null;
        }
        return response.get(key);
    }
}
