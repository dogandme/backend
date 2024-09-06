package com.mungwithme.user.service;

import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final String BEARER = "Bearer_";

    /**
     * 화원가입 및 NONE권한 토큰 발행
     * @param userSignUpDto 가입요청 회원정보
     */
    @Transactional
    public HashMap<String, Object> signUp(UserSignUpDto userSignUpDto, HttpServletRequest request, HttpServletResponse response) throws Exception {

        HashMap<String, Object> result = new HashMap<>();

        // 이메일 중복 확인
        userRepository.findByEmail(userSignUpDto.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("이메일 중복");
                });

        User newUser = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .role(Role.NONE)
                .build();

        newUser.passwordEncode(passwordEncoder);   // 비밀번호 암호화
        newUser = userRepository.save(newUser);    // DB 저장

        // NONE 권한의 토큰 발행(기본정보입력 화면으로 넘어가기 위함)
        String email = newUser.getEmail();
        Role role = newUser.getRole();

        String accessToken = jwtService.createAccessToken(email, role.getKey());   // AccessToken 발급
        String refreshToken = jwtService.createRefreshToken();                             // RefreshToken 발급

        jwtService.setRefreshTokenCookie(response, refreshToken);                          // RefreshToken 쿠키에 저장

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });

        result.put("authorization", accessToken);
        result.put("role", role.getKey());

        return result;
    }

    /**
     * 추가 정보 저장 및 GUEST권한 토큰 발행
     * @param userSignUpDto 추가 회원정보
     */
    public User signUp2(UserSignUpDto userSignUpDto) throws Exception {

        // 닉네임 중복 확인
        userRepository.findByNickname(userSignUpDto.getNickname())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("닉네임 중복");
                });

        // 추가 정보 저장
        return userRepository.findById(userSignUpDto.getUserId())
                .map(user -> {
                    user.setRole(Role.GUEST);
                    user.setNickname(userSignUpDto.getNickname());
                    user.setGender(userSignUpDto.getGender());
                    user.setAge(userSignUpDto.getAge());
                    user.setRegion(userSignUpDto.getRegion());
                    user.setMarketingYn(userSignUpDto.getMarketingYn());

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException("회원 조회 실패"));
    }

    /**
     * 이메일을 이용하여 회원 조회
     *
     * @param email 이메일
     * @return
     */
    public Optional<User> findByEmail(String email) {
         return userRepository.findByEmail(email);
    }
}
