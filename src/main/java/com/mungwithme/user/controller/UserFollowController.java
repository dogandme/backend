package com.mungwithme.user.controller;


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



    /**
     * @param followingUid
     *     팔로우를 당하는 유저 ID
     * @return
     */
    // 팔로우
    @PostMapping("/my-following/{followingUid}")
    public ResponseEntity<?> saveFollowing(@PathVariable(name = "followingUid") Long followingUid) {


        return new ResponseEntity<>(HttpStatus.OK);
    }
    /**
     *
     *
     * 상대방 언 팔로우
     */
    // 팔로우
    @DeleteMapping("/my-following/{following-uid}")
    public ResponseEntity<?> removeFollowing(@PathVariable(name = "following-uid") Long followingUid) {


        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     *
     * 나의 팔로우 리스트에서 나를 팔로우 하는 사용자를 강제 언팔
     *
     */
    @DeleteMapping("/my-followers/{follower-uid}")
    public ResponseEntity<?> removeFollowers(@PathVariable(name = "follower-uid") Long followerUid) {


        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void checkFollowingUid(boolean followingUid) {
        if (followingUid) {
            throw new IllegalArgumentException();
        }
    }




}
