package com.mungwithme.user.controller;


import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.user.model.dto.request.UserPwUpdateDto;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * 유저 설정(management)에 관한 컨트롤러
 *
 * 비밀번호 변경
 * 탈퇴 하기
 * 유저 정보 변경
 *
 *
 */
@Slf4j
@RestController
@RequestMapping("/users/mgmt")
@RequiredArgsConstructor
public class UserMgMtController {


    private final UserService userService;
    private final BaseResponse baseResponse;


}
