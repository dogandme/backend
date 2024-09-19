package com.mungwithme.user.service;


import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserFollows;
import com.mungwithme.user.repository.UserFollowRepository;
import java.util.Optional;
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
    private final UserService userService;

    @Transactional
    public void addFollowing(String followingEmail) {

        // 요청 사용자
        User followerUser = userService.getCurrentUser();

        User followingUser = userService.findByEmail(followingEmail)
            .orElseThrow(() -> new ResourceNotFoundException("ex) 사용자를 찾을수가 없습니다"));

        if (!existsFollowing(followerUser, followingUser)) {
            throw new IllegalArgumentException("ex) 팔로잉을 이미 하셨습니다");
        }

        UserFollows userFollows = UserFollows.create(followerUser, followingUser);

        // 추가
        userFollowRepository.save(userFollows);
    }

    /**
     * 팔로우 삭제 API
     * @param followerUser
     * @param followingUser
     */
    @Transactional
    public void removeFollow(User followerUser, User followingUser) {

        UserFollows userFollows = findByFollowingUser(followerUser, followingUser);

        if (userFollows == null) {
            throw new IllegalArgumentException("ex) 잘못된 요청입니다");
        }
        // 추가
        userFollowRepository.delete(userFollows);
    }



     /**
     *
     *  나의 팔로우 리스트에서 나를 팔로우 하는 사용자를 강제 언팔 API
     * @param followerEmail
     */
    @Transactional
    public void forceUnfollow(String followerEmail) {
        // 요청 사용자
        User followingUser = userService.getCurrentUser();


        User followerUser = userService.findByEmail(followerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("ex) 사용자를 찾을수가 없습니다"));
        removeFollow(followerUser, followingUser);
    }


    /**
     * 상대방 팔로잉 취소 API
     * @param followingEmail
     *          팔로우 당한 유저의 이메일
     *
     */
    @Transactional
    public void cancelFollow(String followingEmail) {
        // 요청 사용자
        User followerUser = userService.getCurrentUser();

        User followingUser = userService.findByEmail(followingEmail)
            .orElseThrow(() -> new ResourceNotFoundException("ex) 사용자를 찾을수가 없습니다"));
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
