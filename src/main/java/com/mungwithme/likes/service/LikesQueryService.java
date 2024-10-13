package com.mungwithme.likes.service;

import com.mungwithme.likes.repository.MarkingLikesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesQueryService {

    private final MarkingLikesQueryRepository likesQueryRepository;

    /**
     * 유저의 좋아요 마킹 id 목록 조회
     * @param userId 유저PK
     * @return 좋아요 마킹 id 목록
     */
    public List<Long> findAllLikesIdsByUserId(Long userId) {
        return likesQueryRepository.findAllByUserIdAndContentType(userId);
    }
}
