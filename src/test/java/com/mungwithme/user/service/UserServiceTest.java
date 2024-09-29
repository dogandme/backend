package com.mungwithme.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    UserService userService;

    @Test
    void getCurrentUser_V2() {
        userQueryService.findCurrentUser_v2();
    }


    @Test
    public void removeUser() {

        // given


        // when

        // then

        userService.removeUser();

    }
}