package com.mungwithme.likes.service;

import com.mungwithme.likes.model.enums.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LikesServiceTest {


    @Autowired
    LikesService likesService;

    @Test
    void addLikes1() {
        likesService.addLikes(16L, ContentType.MARKING);

    }

    @Test
    void addLikes2() {
        likesService.addLikes(16L, ContentType.MARKING);

    }


    @Test
    public void removeLikes() {

        // given
        likesService.deleteLikes(16L, ContentType.MARKING);
        // when

        // then

    }
}