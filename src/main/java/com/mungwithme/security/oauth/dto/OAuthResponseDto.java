package com.mungwithme.security.oauth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * refreshToken 으로 accessToken 을 재발급 받을 때 쓰는 DTO
 *
 */

@Getter
@Setter(AccessLevel.PRIVATE)
public class OAuthResponseDto {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("expires_in")
    private String expires_in;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error")
    private String error;

    @JsonProperty("result")
    private String result;
}
