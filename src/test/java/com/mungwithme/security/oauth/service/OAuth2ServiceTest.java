package com.mungwithme.security.oauth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OAuth2ServiceTest {


    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    UserQueryService userQueryService;

    @Test
    void printClientDetails() {


    }


    @Test
    public void disconnectOAuth2Account() throws UnsupportedEncodingException {
        // given
        User user = userQueryService.findByEmail("lim642666@gmail.com").orElse(null);

        // when
        oAuth2Service.disconnectOAuth2Account(user.getSocialType(),user.getOAuthRefreshToken());

        // then

    }


    @Test
    public void validateRefreshToken() {
        // given
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
        oAuth2Service.renewAccessToken(user.getSocialType(), user.getOAuthRefreshToken());
    }
}