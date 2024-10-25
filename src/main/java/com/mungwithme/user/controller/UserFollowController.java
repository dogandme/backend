package com.mungwithme.user.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.model.dto.response.UserInfoRepPagingDto;
import com.mungwithme.user.model.dto.response.UserInfoResponseDto;
import com.mungwithme.user.service.UserFollowService;
import com.mungwithme.user.service.UserFollowsQueryService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/follows")
public class UserFollowController {

    private final UserFollowService userFollowService;
    private final UserFollowsQueryService userFollowsQueryService;

    private final BaseResponse baseResponse;

    /**
     * 유저 이메일을 이용하여 유저 팔로잉
     *
     * @param followingNickname
     *     팔로잉을 당할 유저
     * @return
     */
    @PostMapping("/my-followings/{following-nickname}")
    public ResponseEntity<CommonBaseResult> createFollowing(@PathVariable(name = "following-nickname") String followingNickname,
        HttpServletRequest request)
        throws IOException {
        userFollowService.addFollowing(followingNickname);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "follow.save.success", request.getLocale());

    }

    /**
     * 내가 팔로잉을 한 유저를 언팔로우
     *
     * @param followingNickname
     *     팔로잉을 당한 유저
     * @return
     * @throws IOException
     */
    @DeleteMapping("/my-followings/{following-nickname}")
    public ResponseEntity<CommonBaseResult> deleteFollowing(
        @PathVariable(name = "following-nickname") String followingNickname, HttpServletRequest request) throws IOException {
        userFollowService.removeFollow(followingNickname);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "follow.remove.success",
            request.getLocale());

    }

    /**
     * 나의 팔로우 리스트에서 나를 팔로우 하는 사용자를 강제 언팔
     *
     * @param followerNickname
     *     팔로워 리스트에서 삭제될 팔로워 유저
     * @return
     */
    @DeleteMapping("/my-followers/{follower-nickname}")
    public ResponseEntity<CommonBaseResult> deleteFollowers(
        @PathVariable(name = "follower-nickname") String followerNickname, HttpServletRequest request) throws IOException {
        userFollowService.forceUnfollow(followerNickname);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "follow.unfollow.success",
            request.getLocale());

    }

    /**
     * 유저가 팔로우 하고 있는 유저 목록
     *
     * @return
     */
    @GetMapping("/followings/{nickname}")
    public ResponseEntity<CommonBaseResult> getFollowings(@PathVariable(name = "nickname") String nickname,
        @RequestParam(name="offset",defaultValue = "0") int offset
    ) throws IOException {
        int size = 20;

        UserInfoRepPagingDto followingUsers = userFollowsQueryService.findFollowingUsers(nickname, offset, size);

        int statusCode = HttpStatus.OK.value();
        if (followingUsers.getUserInfos().isEmpty()) {
            statusCode = HttpStatus.NO_CONTENT.value();
        }
        return baseResponse.sendContentResponse(followingUsers, statusCode);
    }


    /**
     * 유저를 팔로우 하고 있는 유저 목록
     *
     * @return
     */
    @GetMapping("/followers/{nickname}")
    public ResponseEntity<CommonBaseResult> getFollowers(@PathVariable(name = "nickname") String nickname,
        @RequestParam(name="offset",defaultValue = "0") int offset
    ) throws IOException {
        int size = 20;

        UserInfoRepPagingDto followerUsers = userFollowsQueryService.findFollowerUsers(nickname, offset, size);

        int statusCode = HttpStatus.OK.value();

        if (followerUsers.getUserInfos().isEmpty()) {
            statusCode = HttpStatus.NO_CONTENT.value();
        }
        return baseResponse.sendContentResponse(followerUsers, statusCode);
    }

}
