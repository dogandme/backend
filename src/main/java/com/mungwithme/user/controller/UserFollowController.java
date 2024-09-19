package com.mungwithme.user.controller;


import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.service.UserFollowService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/follows")
public class UserFollowController {
    private final UserFollowService userFollowService;

    private final BaseResponse baseResponse;

    /**
     * 유저 이메일을 이용하여 유저 팔로잉
     *
     * @param followingEmail
     *     팔로잉을 당할 유저
     * @return
     */
    @PostMapping("/my-followings/{following-email}")
    public ResponseEntity<CommonBaseResult> saveFollowing(@PathVariable(name = "following-email") String followingEmail)
        throws IOException {
        try {
            userFollowService.addFollowing(followingEmail);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

    /**
     * 내가 팔로잉을 한 유저를 언팔로우
     *
     * @param followingEmail
     *     팔로잉을 당한 유저
     * @return
     * @throws IOException
     */
    @DeleteMapping("/my-followings/{following-email}")
    public ResponseEntity<CommonBaseResult> cancelFollowing(
        @PathVariable(name = "following-email") String followingEmail) throws IOException {
        try {
            userFollowService.cancelFollow(followingEmail);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

    /**
     * 나의 팔로우 리스트에서 나를 팔로우 하는 사용자를 강제 언팔
     *
     * @param followerEmail
     *         팔로워 리스트에서 삭제될 팔로워 유저
     * @return
     */
    @DeleteMapping("/my-followers/{follower-email}")
    public ResponseEntity<CommonBaseResult> removeFollowers(
        @PathVariable(name = "follower-email") String followerEmail) throws IOException {
        try {
            userFollowService.forceUnfollow(followerEmail);
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return baseResponse.sendErrorResponse(400, e.getMessage());
        }
    }

}
