package com.mungwithme.likes.service;

import com.mungwithme.likes.model.enums.ContentType;
import com.mungwithme.likes.repository.LikesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesQueryService {

    private final LikesQueryRepository likesQueryRepository;

    /**
     * 유저의 좋아요 마킹 id 목록 조회
     * @param userId 유저PK
     * @return 좋아요 마킹 id 목록
     */
    public List<Long> findAllLikesIdsByUserId(Long userId) {
        return likesQueryRepository.findByUserIdAndContentType(userId, ContentType.MARKING);
    }
}
