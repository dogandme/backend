package com.mungwithme.user.service;


import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
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
    private final UserQueryService userQueryService;

    /**
     * 팔로우 추가
     * @param followingNickname
     */
    @Transactional
    public void addFollowing(String followingNickname) {

        // 요청 사용자
        User followerUser = userQueryService.findCurrentUser();

        User followingUser = userQueryService.findByNickname(followingNickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));


        // 자기 자신을 팔로잉 할 경우
        if (followerUser.getId().equals(followingUser.getId())){
            throw new IllegalArgumentException("error.arg");
        }

        if (!existsFollowing(followerUser, followingUser)) {
            return;
        }

        UserFollows userFollows = UserFollows.create(followerUser, followingUser);

        // 추가
        userFollowRepository.save(userFollows);
    }

    /**
     * 팔로우 삭제 API
     *
     * @param followerUser
     * @param followingUser
     */
    @Transactional
    public void removeFollow(User followerUser, User followingUser) {

        UserFollows userFollows = findByFollowingUser(followerUser, followingUser);

        if (userFollows == null) {
            return;
        }
        // 추가
        userFollowRepository.delete(userFollows);
    }


    /**
     * 나의 팔로우 리스트에서 나를 팔로우 하는 사용자를 강제 언팔 API
     *
     */
    @Transactional
    public void forceUnfollow(String followerNickname) {
        // 요청 사용자
        User followingUser = userQueryService.findCurrentUser();

        User followerUser = userQueryService.findByNickname(followerNickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));
        removeFollow(followerUser, followingUser);
    }


    /**
     * 상대방 팔로잉 취소 API
     *
     * @param followingNickname
     *     팔로우 당한 유저의 닉네임
     */
    @Transactional
    public void removeFollow(String followingNickname) {
        // 요청 사용자
        User followerUser = userQueryService.findCurrentUser();

        User followingUser = userQueryService.findByNickname(followingNickname)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));
        removeFollow(followerUser, followingUser);
    }

    /**
     * 팔로잉 유무 확인
     *
     * @return
     */
    public boolean existsFollowing(User followerUser, User followingUser) {

        UserFollows byFollowingUser = findByFollowingUser(followerUser, followingUser);
        return byFollowingUser == null;
    }

    /**
     * 팔로잉 유저 기준으로 검색
     *
     * @return
     */
    public UserFollows findByFollowingUser(User followerUser, User followingUser) {
        return userFollowRepository.findByFollowingUser(followingUser, followerUser).orElse(null);

    }


}
