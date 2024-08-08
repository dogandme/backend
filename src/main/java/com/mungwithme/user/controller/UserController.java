package com.mungwithme.user.controller;

import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BaseResponse baseResponse;

    /**
     * 회원가입 요청
     * @param userSignUpDto 가입요청 회원정보
     * @return 회원가입 성공 시 공통 응답 메세지
     */
    @PostMapping("/users")
    public CommonBaseResult signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return baseResponse.getSuccessResult();
    }
}
