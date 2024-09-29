package com.mungwithme.user.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserQueryServiceTest {

    @Autowired
    UserQueryService userQueryService;



    @Test
    public void findMyInfo() {

        userQueryService.findMyInfo();
    // given

    // when

    // then

    }
}