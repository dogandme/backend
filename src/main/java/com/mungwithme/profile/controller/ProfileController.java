package com.mungwithme.profile.controller;

import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.profile.service.ProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileQueryService profileQueryService;
    private final BaseResponse baseResponse;

    /**
     * 프로필 대시보드 조회
     * @param nickname nickname
     */
    @GetMapping("")
    public ResponseEntity<CommonBaseResult> getProfile(@Validated @RequestParam(name = "nickname") String nickname) throws IOException {
        return baseResponse.sendContentResponse(profileQueryService.findProfileByNickname(nickname), HttpStatus.OK.value());
    }
}
