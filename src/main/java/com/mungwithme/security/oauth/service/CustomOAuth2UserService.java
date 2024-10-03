package com.mungwithme.security.oauth.service;


import com.mungwithme.security.oauth.dto.OAuth2UserInfo;
import com.mungwithme.security.oauth.dto.PrincipalDetails;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
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
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;


    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    private static final String NAVER = "naver";
    private static final String GOOGLE = "google";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        // 1. 회원 정보(attributes) 가져오기
        Map<String, Object> attributes = super.loadUser(oAuth2UserRequest).getAttributes();

        // 2. registrationId(third-party id) 가져오기
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration()
            .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 5. 회원 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, attributes);

        // 6. 회원가입 및 로그인
        User user = userService.getOrSave(oAuth2UserInfo);

        // 7. OAuth2User로 반환
        return new PrincipalDetails(user, attributes, userNameAttributeName);
    }


}
