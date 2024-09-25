package com.mungwithme.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.email.EmailAuthRequestDto;
import com.mungwithme.common.email.EmailRequestDto;
import com.mungwithme.common.email.EmailService;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.security.jwt.PasswordUtil;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BaseResponse baseResponse;
    private final EmailService emailService;
    private final JwtService jwtService;

    /**
     * 회원가입 1단계 : [일반] 이메일/비밀번호 저장
     *
     * @param userSignUpDto
     *     가입요청 회원정보
     * @return 공통 응답 메세지
     */
    @PostMapping("")
    public ResponseEntity<CommonBaseResult> signUp(@RequestBody UserSignUpDto userSignUpDto,
        HttpServletResponse response) throws Exception {
        String email = userSignUpDto.getEmail();
        String password = userSignUpDto.getPassword();
        // email, password null 체크
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("error.arg.signUp");
        }
        UserResponseDto userResponseDto = userService.signUp(userSignUpDto, response); // 회원가입
        return baseResponse.sendContentResponse(userResponseDto, HttpStatus.OK.value());
    }

    /**
     * 회원가입 이메일 인증 코드 전송
     */
    @PostMapping("/auth")
    public ResponseEntity<CommonBaseResult> mailSend(@RequestBody @Validated EmailRequestDto emailDto) throws IOException {
        // 이메일 중복
        Optional<User> user = userService.findByEmail(emailDto.getEmail());
        if (user.isPresent()) {
            throw new DuplicateResourceException("error.duplicate.email");
        }

        emailService.joinEmail(emailDto.getEmail()); // 인증코드 이메일 전송

        return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
    }

    /**
     * 회원가입 이메일 인증 코드 검증
     */
    @PostMapping("/auth/check")
    public ResponseEntity<CommonBaseResult> mailAuth(@RequestBody @Valid EmailAuthRequestDto emailAuthRequestDto)
        throws IOException {
        Boolean checked = emailService.checkAuthNum(emailAuthRequestDto);
        if (checked) {
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
        } else {
            // 변경) 401 -> 400
            throw new IllegalArgumentException("error.arg.authCheck");
        }
    }

    /**
     * 회원가입 2단계 : [일반/소셜] 추가 정보 저장
     *
     * @param userSignUpDto
     *     추가회원정보
     */
    @PutMapping("/additional-info")
    public ResponseEntity<CommonBaseResult> signUp2(@RequestBody UserSignUpDto userSignUpDto) throws Exception {

        UserResponseDto userResponseDto = new UserResponseDto();

        userSignUpDto.setUserId(userService.getCurrentUser().getId());  // UserDetails에서 유저 정보 조회
        User user = userService.signUp2(userSignUpDto);                 // 추가 정보 저장

        userResponseDto.setRole(user.getRole().getKey());
        userResponseDto.setNickname(user.getNickname());

        return baseResponse.sendContentResponse(userResponseDto, HttpStatus.OK.value());

    }

    /**
     * 임시 비밀번호 이메일 발송
     *
     * @param emailDto
     *     수신 이메일
     */
    @PostMapping("/password")
    public ResponseEntity<CommonBaseResult> sendTemporaryPassword(@RequestBody @Valid EmailRequestDto emailDto)
        throws IOException {

        String email = emailDto.getEmail();

        // 1. 이메일로 일반 회원 조회 (소셜 회원은 임시 비밀번호 설정 불가)
        Optional<User> user = userService.findByEmailAndSocialTypeIsNull(email);

        // 2. 실패 시 실패 응답 return
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.user");
        }

        // 3. 성공 시 임시 비밀번호 생성
        String temporaryPassword = PasswordUtil.generateRandomPassword();
        log.info("temporaryPassword : {}", temporaryPassword);

        // 4. 임시 비밀번호 DB 업데이트
        userService.updatePasswordByEmail(email, temporaryPassword);

        // 5. 임시 비밀번호 이메일 전송
        emailService.temporaryPasswordEmail(email, temporaryPassword);

        // 6. 성공 응답 return
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
    }

    /**
     * 닉네임 중복 검사
     *
     * @param userSignUpDto
     *     닉네임
     */
    @PostMapping("/nickname")
    public ResponseEntity<CommonBaseResult> checkNicknameDuplicate(@RequestBody UserSignUpDto userSignUpDto)
        throws IOException {
        Optional<User> user = userService.findByNickname(userSignUpDto.getNickname());

        if (user.isPresent()) {
            throw new DuplicateResourceException("error.duplicate.nickname");
        } else {
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
        }
    }
}
