package com.mungwithme.user.controller;

import com.mungwithme.common.annotation.authorize.NoneAuthorize;
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
import com.mungwithme.user.model.dto.request.UserDeleteDto;
import com.mungwithme.user.model.dto.request.UserPwUpdateDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final BaseResponse baseResponse;
    private final EmailService emailService;

    /**
     * 회원가입 1단계 : [일반] 이메일/비밀번호 저장
     *
     * @param userSignUpDto
     *     가입요청 회원정보
     * @return 공통 응답 메세지
     */
    @PostMapping("")
    public ResponseEntity<CommonBaseResult> createUserSingUp(@RequestBody UserSignUpDto userSignUpDto,
        HttpServletResponse response,HttpServletRequest request) throws Exception {
        String email = userSignUpDto.getEmail();
        String password = userSignUpDto.getPassword();
        // email, password null 체크
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("error.arg.signUp");
        }
        UserResponseDto userResponseDto = userService.signUp(userSignUpDto, response,request); // 회원가입
        return baseResponse.sendContentResponse(userResponseDto, HttpStatus.OK.value());
    }

    /**
     * 회원가입 이메일 인증 코드 전송
     */
    @PostMapping("/auth")
    public ResponseEntity<CommonBaseResult> mailSend(@RequestBody @Validated EmailRequestDto emailDto)
        throws IOException {
        // 이메일 중복
        Optional<User> user = userQueryService.findByEmail(emailDto.getEmail());
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
    public ResponseEntity<CommonBaseResult> mailAuth(@RequestBody @Validated EmailAuthRequestDto emailAuthRequestDto)
        throws IOException {
        Boolean checked = emailService.checkAuthNum(emailAuthRequestDto);
        if (checked) {
            return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
        } else {
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
    public ResponseEntity<CommonBaseResult> createUserInfoSignUp2(@RequestBody UserSignUpDto userSignUpDto,HttpServletRequest request)
        throws Exception {
        return baseResponse.sendContentResponse(userService.signUp2(userSignUpDto,request), HttpStatus.OK.value());

    }

    /**
     * 임시 비밀번호 이메일 발송
     *
     * @param emailDto
     *     수신 이메일
     */
    @PostMapping("/password")
    public ResponseEntity<CommonBaseResult> sendTemporaryPassword(@RequestBody @Validated EmailRequestDto emailDto)
        throws IOException {

        String email = emailDto.getEmail();

        // 1. 이메일로 일반 회원 조회 (소셜 회원은 임시 비밀번호 설정 불가)
        Optional<User> user = userQueryService.findByEmailAndSocialTypeIsNull(email);

        // 2. 실패 시 실패 응답 return
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.user");
        }

        // 3. 성공 시 임시 비밀번호 생성
        String temporaryPassword = PasswordUtil.generateRandomPassword();

        // 4. 임시 비밀번호 DB 업데이트
        userService.editPasswordByEmail(email, temporaryPassword);

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
        userQueryService.duplicateNickname(userSignUpDto.getNickname());

        return baseResponse.sendSuccessResponse(HttpStatus.OK.value());
    }


    /**
     * 유저 탈퇴 API
     * <p>
     * 소셜 회원일 경우
     * 이메일 회원일 경우
     * 마킹 삭제
     * 펫 삭제
     * Address 삭제
     * likes 삭제
     * image 삭제
     */
    @DeleteMapping("/me")
    public ResponseEntity<CommonBaseResult> deleteUsers(@RequestBody UserDeleteDto userDeleteDto,
        HttpServletRequest request)
        throws IOException {
        userService.removeUser(userDeleteDto);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "user.delete.success", request.getLocale());
    }


}
