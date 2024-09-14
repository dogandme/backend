package com.mungwithme.user.service;


import com.mungwithme.user.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFollowService {


    private final UserFollowRepository userFollowRepository;

    @Transactional
    public void addFollowerActions(Long followingUid) {
        // 팔로우를 신청한 사용자
        User fromUser = userQueryService.findOne();
        // 사용자가 같은경우 예외 처리
        checkFollowingUid(fromUser.getId().equals(followingUid));
        // 팔로우 대상자
        User toUser = userQueryService.findOne(followingUid, Enabled.ENABLED);

        addFollows(fromUser, toUser);


    }




}
