package com.mungwithme.security.oauth.service;


import com.mungwithme.security.oauth.dto.CustomOAuth2User;
import com.mungwithme.security.oauth.dto.NaverResponse;
import com.mungwithme.security.oauth.dto.OAuth2Response;
import com.mungwithme.security.oauth.dto.OAuthAttributes;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.SocialType;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * oauth2로그인시 소셜로그인 회사에서 제공해주는 정보가 오는곳.
 *
 * @modification.author 장수현
 * @modification.date 2024.8.12
 * @modification.details OAuthAttributes을 이용하여 소셜 회원 정보 분기
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String GOOGLE = "google";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes();                    // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttritbutes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        OAuth2Response oAuth2Response = extractAttritbutes.getOAuth2Response();

        User existData = userRepository.findByEmail(extractAttritbutes.getOAuth2Response().getEmail())
                .orElseGet(() -> {  // 기존에 등록되지 않은 회원일 경우 신규 생성
                    return User.builder()
                            .email(oAuth2Response.getEmail())
                            .nickname(oAuth2Response.getName())
                            .role(Role.GUEST)
                            .socialType(socialType)
                            .socialId(oAuth2Response.getProviderId())
                            .imageUrl(oAuth2Response.getImageUrl())
                            .build();
                });

        userRepository.save(existData);

        return new CustomOAuth2User(existData);
    }

    private SocialType getSocialType(String registrationId) {
        if (NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        return SocialType.GOOGLE;
    }

}
