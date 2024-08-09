package com.mungwithme.security.oauth.dto;

import java.util.Map;
/**
 네이버는 값이 response에 담아오기 때문에 한번 값을 필터링해준다.
 */
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");

    }


//    소셜로그인업체
    @Override
    public String getProvider() {
        return "naver";
    }

//    각 소셜로그인쪽에서 제공해주는 id
    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

//    이메일
    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

//    실명
    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
