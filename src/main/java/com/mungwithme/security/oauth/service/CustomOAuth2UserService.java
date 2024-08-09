package com.mungwithme.security.oauth.service;


import com.mungwithme.security.oauth.dto.CustomOAuth2User;
import com.mungwithme.security.oauth.dto.NaverResponse;
import com.mungwithme.security.oauth.dto.OAuth2Response;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.SocialType;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;




/**
 * oauth2로그인시 소셜로그인 회사에서 제공해주는 정보가 오는곳.
 */

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;




    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {



        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);


        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

//      http://localhost:8080/ouath/login/naver

//      oAuth2User.getAttributes() = {resultcode=00,
//      message=success, response={id=hf7nANvFeStDAEdqfmh4zMx-5wqiW4CBfjYUYvfilQc,
//      email=goorm94@naver.com, name=전형근}}


        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }


        else if(registrationId.equals("google")) {


        }

        else {

            return null;
        }


        User existData = userRepository.findByEmail(oAuth2Response.getEmail())
                .orElse(null); // 존재하지 않을 경우 null 반환


        if (existData == null) {


            User user = User.builder()
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getProviderId())
                    .role(Role.GUEST)
                    .socialType(SocialType.NAVER)
                    .socialId(oAuth2Response.getProviderId())
                    .build();


            userRepository.save(user);



            return new CustomOAuth2User(user);
        }
        else {


            userRepository.save(existData);


            return new CustomOAuth2User(existData);

        }


    }

}
