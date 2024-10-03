package com.mungwithme.security.jwt.service;


import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    JwtService jwtService;

    @Test
    void extractJwt() {

        String accessToken = jwtService.createAccessToken("2221325@naver.com", Role.USER.getKey());

        System.out.println("accessToken = " + accessToken);

        String token = accessToken.replace("Bearer_", "");

        jwtService.getJwtClaim(token);





    }
}