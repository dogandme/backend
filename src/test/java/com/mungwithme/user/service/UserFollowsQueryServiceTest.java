package com.mungwithme.user.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.user.model.dto.response.UserInfoResponseDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserFollowsQueryServiceTest {


    @Autowired
    UserFollowsQueryService userFollowsQueryService;
    @Test
    void findFollowingUsers() {

//        List<UserInfoResponseDto> list = userFollowsQueryService.findFollowingUsers("임하늘음임임", 0, 10);


    }
}