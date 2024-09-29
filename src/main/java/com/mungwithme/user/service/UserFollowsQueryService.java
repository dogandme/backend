package com.mungwithme.user.service;

import com.mungwithme.user.repository.UserFollowQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowsQueryService {

    private final UserFollowQueryRepository userFollowQueryRepository;

    /**
     * 유저의 팔로워 목록 조회
     * @param userId 유저PK
     * @return 팔로워 id 목록
     */
    public List<Long> findAllFollowersByUserId(Long userId) {
        return userFollowQueryRepository.findIdsByFollowingUserId(userId);
    }

    /**
     * 유저의 팔로잉 목록 조회
     * @param userId 유저PK
     * @return 팔로잉 id 목록
     */
    public List<Long> findAllFollowingsByUserId(Long userId) {
        return userFollowQueryRepository.findIdsByFollowerUserId(userId);
    }
}
