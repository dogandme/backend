package com.mungwithme.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserFollowServiceTest {

    @Autowired
    UserFollowService userFollowService;

    @Test
    void addFollowing() {
        userFollowService.addFollowing("2221325@naver.com");
    }


    @Test
    public void forceUnfollow() {
        // given
        userFollowService.forceUnfollow("lim642666@gmail.com");
        // when
        // then
    }

    @Test
    public void cancelFollow() {
        // given
        userFollowService.removeFollow("2221325@naver.com");
        // when
        // then
    }
}