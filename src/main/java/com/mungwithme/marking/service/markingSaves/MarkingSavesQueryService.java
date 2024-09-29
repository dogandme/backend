package com.mungwithme.marking.service.markingSaves;

import com.mungwithme.marking.repository.markingSaves.MarkingSavesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkingSavesQueryService {

    private final MarkingSavesQueryRepository markingSavesQueryRepository;

    /**
     * 유저의 북마크 목록 조회
     * @param userId 유저PK
     * @return 북마크 id 목록 조회
     */
    public List<Long> findAllBookmarksIdsByUserId(Long userId) {
        return markingSavesQueryRepository.findAllByUserId(userId);
    }
}
