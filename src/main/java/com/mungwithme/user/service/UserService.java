package com.mungwithme.user.service;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.TokenUtils;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.dto.request.UserPwUpdateDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;
    private final JwtService jwtService;
    private final AddressRepository addressRepository;

    private static final String BEARER = "Bearer_";

    /**
     * 화원가입 및 NONE권한 토큰 발행
     * @param userSignUpDto 가입요청 회원정보
     */
    @Transactional
    public UserResponseDto signUp(UserSignUpDto userSignUpDto, HttpServletResponse response) throws Exception {

        UserResponseDto userResponseDto = new UserResponseDto();

        // 이메일 중복 확인
        userQueryService.findByEmail(userSignUpDto.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("error.duplicate.email");
                });

        User newUser = User.builder()
                .token(TokenUtils.getToken())
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

        userQueryService.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });

        userResponseDto.setAuthorization(accessToken);
        userResponseDto.setRole(role.getKey());

        return userResponseDto;
    }

    /**
     * 추가 정보 저장 및 GUEST권한 토큰 발행
     * @param userSignUpDto 추가 회원정보
     */
    @Transactional
    public User signUp2(UserSignUpDto userSignUpDto) throws Exception {

        // 닉네임 중복 확인
        userRepository.findByNickname(userSignUpDto.getNickname())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("error.duplicate.nickname");
                });

        // 추가 정보 저장
        return userQueryService.findById(userSignUpDto.getUserId())
                .map(user -> {
                    // 요청 데이터에서 region ID 리스트를 가져옴
                    List<Long> regionIds = userSignUpDto.getRegion();

                    // region ID를 기반으로 Address 엔터티 조회
                    Set<Address> addresses = regionIds.stream()
                            .map(addressId -> addressRepository.findById(addressId)
                                    .orElseThrow(() -> new ResourceNotFoundException("error.notfound.address")))
                            .collect(Collectors.toSet());

                    user.setRole(Role.GUEST);
                    user.setNickname(userSignUpDto.getNickname());
                    user.setGender(userSignUpDto.getGender());
                    user.setAge(userSignUpDto.getAge());
                    user.setRegions(addresses);
                    user.setMarketingYn(userSignUpDto.getMarketingYn());

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));
    }




    /**
     * 이메일을 이용하여 비밀번호 업데이트
     * @param email 이메일
     * @param password 신규 비밀번호
     */
    @Transactional
    public void editPasswordByEmail(String email, String password) {
        userQueryService.findByEmail(email) // 이메일을 이용하여 회원 조회
            .ifPresent(user -> user.updatePw(password, passwordEncoder));
    }

    /**
     * 비밀번호 변경 API
     *
     *
     * @param userPwUpdateDto
     */
    @Transactional
    public void editPassword(UserPwUpdateDto userPwUpdateDto) {
        User currentUser = userQueryService.findCurrentUser();
        if (currentUser.getSocialType() != null) {
            throw new IllegalArgumentException("error.arg.social.pw");
        }

        String password = userPwUpdateDto.getPassword();

        String newPw = userPwUpdateDto.getNewPw();
        String newPwChk = userPwUpdateDto.getNewPwChk();

        boolean matches = passwordEncoder.matches(password, currentUser.getPassword());

        // 현재 비밀번호와 변경하려는 비밀번호가 같은 경우
        // 비밀번호가 일치하지 않는 경우
        // 변경 비밀번호와 변경확인 비밀번호 입력값이 다른 경우
        if (!newPw.equals(newPwChk) || !matches || newPw.equals(password)) {
            throw new IllegalArgumentException("error.arg.change.pw");
        }

        // 비밀번호 업데이트
        currentUser.updatePw(newPw,passwordEncoder);
    }

    /**
     * 유저 탈퇴 API
     *
     *
     * 펫 삭제
     *
     * address 삭제
     * 마킹 삭제
     * 저장 삭제
     * 좋아요 삭제
     *
     */
    @Transactional
    public void removeUsers() {
        User currentUser = userQueryService.findCurrentUser();




    }


}
