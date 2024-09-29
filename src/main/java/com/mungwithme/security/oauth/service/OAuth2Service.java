package com.mungwithme.security.oauth.service;


import com.mungwithme.security.oauth.dto.OAuthResponseDto;
import com.mungwithme.user.model.SocialType;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Provider;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * OAuth2ClientProperties 를 사용하여
 * Oauth 설정 값 사용
 **/
@Slf4j
@Service
//@RequiredArgsConstructor
public class OAuth2Service {


    @Value("${google.oAuth.url}")
    private final String GOOGLE_REVOKE_URI;
    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final RestTemplate restTemplate;

    public OAuth2Service(@Value("${google.oAuth.url}") String GOOGLE_REVOKE_URI,
        OAuth2ClientProperties oAuth2ClientProperties, RestTemplate restTemplate) {
        this.GOOGLE_REVOKE_URI = GOOGLE_REVOKE_URI;
        this.oAuth2ClientProperties = oAuth2ClientProperties;
        this.restTemplate = restTemplate;
    }


    public void validateAccessToken(String oAuthKey) {

        return;
    }


    /**
     *
     * 로그인 연동해제 API
     *
     * oAuthRefreshToken 으로
     * accessToken 을 새로 발급 받은 뒤
     * accessToken 으로 연동 해제
     *
     * @param socialType
     * @param oAuthRefreshToken
     * @throws UnsupportedEncodingException
     */
    public void disconnectOAuth2Account(SocialType socialType, String oAuthRefreshToken) {
        String oAuthKey = socialType.getType();
        Registration registration = oAuth2ClientProperties.getRegistration().get(oAuthKey);
        Provider provider = oAuth2ClientProperties.getProvider().get(oAuthKey);

        OAuthResponseDto oAuthResponseDto = renewAccessToken(socialType, oAuthRefreshToken);


        MultiValueMap<String, String> params = createParams("delete", oAuthRefreshToken, registration);
        String revokeUri = null;

        // 불필요한 파라미터 제거
        params.remove("refresh_token");
        // 새로 발급받은 accessToken 추가
        String tokenKey = "access_token";

        switch (socialType) {
            case GOOGLE:
                revokeUri = GOOGLE_REVOKE_URI;
                tokenKey = "token";
                break;
            default:
                revokeUri = provider.getTokenUri();
                params.add("service_provider", socialType.name());
                break;
        }
        params.add(tokenKey, oAuthResponseDto.getAccessToken());


        getOAuthResponseDto(revokeUri, params);
    }

    public OAuthResponseDto renewAccessToken(SocialType socialType, String oAuthRefreshToken) {

        String oAuthKey = socialType.getType();
        Registration registration = oAuth2ClientProperties.getRegistration().get(oAuthKey);
        Provider provider = oAuth2ClientProperties.getProvider().get(oAuthKey);

        MultiValueMap<String, String> params = createParams(
            "refresh_token",
            oAuthRefreshToken,
            registration);

        return getOAuthResponseDto(provider.getTokenUri(), params);
    }

    private OAuthResponseDto getOAuthResponseDto(String requestUrl, MultiValueMap<String, String> params) {

        // 1. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3. 요청 엔티티 생성 (헤더 + 바디)
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        try {
            URI uri = new URI(requestUrl);
            ResponseEntity<OAuthResponseDto> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                OAuthResponseDto.class
            );

            log.info("response.getBody().getRefreshToken() = {}", response.getBody().getRefreshToken());
            log.info("response.getBody().getAccessToken() = {}", response.getBody().getAccessToken());
            log.info("response.getStatusCode() = {}", response.getStatusCode());

            if (response.getBody().getError() != null) {
                throw new Exception("error");
            }
            return response.getBody();
        } catch (URISyntaxException e) {
            log.info("잘못된 URL 방식입니다.");
            throw new RuntimeException("error");
        } catch (Exception e) {
            log.info("e.getClass() = {}", e.getClass());
            log.info("연동 중 문제 발생");
            throw new RuntimeException("error");
        }
    }

    private static MultiValueMap<String, String> createParams(
        String grant_type,
        String oAuthRefreshToken,
        Registration registration) {
        String clientId = registration.getClientId();
        String clientSecret = registration.getClientSecret();
        // 2. 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("refresh_token", oAuthRefreshToken);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", grant_type);
        return params;
    }


}
