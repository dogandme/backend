package com.mungwithme.user.model;

/**
 * 소셜 채널 종류
 */
public enum SocialType {
    KAKAO("kakao"), GOOGLE("google"), NAVER("naver");

    private String type;

    SocialType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }



}
