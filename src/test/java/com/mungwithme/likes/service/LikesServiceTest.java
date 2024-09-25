package com.mungwithme.likes.service;

import com.mungwithme.likes.model.dto.response.LikeCountResponseDto;
import com.mungwithme.likes.model.enums.ContentType;
import java.util.HashSet;
import java.util.Set;
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
    void fetchLikeCounts() {

        Set<Long> contentsIds = new HashSet<>();
        contentsIds.add(16L);
        contentsIds.add(11L);
        contentsIds.add(12L);
        contentsIds.add(13L);
        contentsIds.add(15L);
        Set<LikeCountResponseDto> contentLikeCountResponseDtos = likesService.findLikeCounts(contentsIds,
            ContentType.MARKING);

        for (LikeCountResponseDto likeCountResponseDto : contentLikeCountResponseDtos) {

            System.out.println(" ======================== " );
            System.out.println("likeCountResponseDto.getContentId() = " + likeCountResponseDto.getContentId());
            System.out.println("likeCountResponseDto.getCount() = " + likeCountResponseDto.getCount());
            System.out.println("likeCountResponseDto.getContentType() = " + likeCountResponseDto.getContentType());

        }


    }


    @Test
    public void removeLikes() {

        // given
        likesService.removeLikes(16L, ContentType.MARKING);
        // when

        // then

    }
}