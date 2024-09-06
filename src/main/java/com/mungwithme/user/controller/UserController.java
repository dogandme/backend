package com.mungwithme.user.controller;

import com.mungwithme.common.email.EmailAuthRequestDto;
import com.mungwithme.common.email.EmailRequestDto;
import com.mungwithme.common.email.EmailService;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
     * @param userSignUpDto 가입요청 회원정보
     * @return 공통 응답 메세지
     */
    @PostMapping("")
    public CommonBaseResult signUp(@RequestBody UserSignUpDto userSignUpDto, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String email = userSignUpDto.getEmail();        // 이메일
        String password = userSignUpDto.getPassword();  // 패스워드

        // email, password null 체크
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return baseResponse.getFailResult(400, "이메일/비밀번호는 필수 입력 항목입니다.");
        }

        try {

            HashMap<String, Object> result = userService.signUp(userSignUpDto, request, response); // 회원가입
            return baseResponse.getContentResult(result);

        } catch (DuplicateResourceException e) {
            return baseResponse.getFailResult(409, "이미 존재하는 이메일입니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.getFailResult(400, "error");
        }
    }

    /**
     * 회원가입 이메일 인증 코드 전송
     */
    @PostMapping("/auth")
    public CommonBaseResult mailSend(@RequestBody @Valid EmailRequestDto emailDto) {// 이메일 발송
        try {

            // 이메일 중복
            Optional<User> user = userService.findByEmail(emailDto.getEmail());
            if (!user.isEmpty()) {
                return baseResponse.getFailResult(409, "이미 존재하는 이메일입니다.");
            }

            emailService.joinEmail(emailDto.getEmail()); // 인증코드 이메일 전송

        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.getFailResult(400, "error");
        }

        return baseResponse.getSuccessResult();
    }

    /**
     * 회원가입 이메일 인증 코드 검증
     */
    @PostMapping("/auth/check")
    public CommonBaseResult mailAuth(@RequestBody @Valid EmailAuthRequestDto emailAuthRequestDto) {
        Boolean checked = emailService.checkAuthNum(emailAuthRequestDto);
        return checked?baseResponse.getSuccessResult():baseResponse.getFailResult(401, "이메일 인증 실패");
    }

    /**
     * 회원가입 2단계 : [일반/소셜] 추가 정보 저장
     * @param userSignUpDto 추가회원정보
     */
    @PutMapping("/additional-info")
    public CommonBaseResult signUp2(@RequestBody UserSignUpDto userSignUpDto,
                                    HttpServletRequest request) throws Exception {

        HashMap<String, Object> result = new HashMap<>();

        try {

            // jwt에서 userId 가져오기
            Long userId = jwtService.findUserIdByEmailFromJwt(request);

            userSignUpDto.setUserId(userId);
            User user = userService.signUp2(userSignUpDto); // 추가 정보 저장

            result.put("role", user.getRole().getKey());
            result.put("nickname", user.getNickname());
            return baseResponse.getContentResult(result);
        } catch (DuplicateResourceException e) {
            return baseResponse.getFailResult(409, "중복된 닉네임 입니다.");
        } catch (ResourceNotFoundException e) {
            return baseResponse.getFailResult(404, "회원을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.getFailResult(400, "error");
        }
    }
}
