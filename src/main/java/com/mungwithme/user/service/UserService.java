package com.mungwithme.user.service;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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
        userRepository.findByEmail(userSignUpDto.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("error.duplicate.email");
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
        return userRepository.findById(userSignUpDto.getUserId())
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
     * 이메일을 이용하여 회원 조회
     *
     * @param email 이메일
     * @return 조회된 회원
     */
    public Optional<User> findByEmail(String email) {
         return userRepository.findByEmail(email);
    }

    /**
     * 이메일을 이용하여 비밀번호 업데이트
     * @param email 이메일
     * @param password 신규 비밀번호
     */
    @Transactional
    public void updatePasswordByEmail(String email, String password) {
        findByEmail(email) // 이메일을 이용하여 회원 조회
                .map(user -> {
                    user.setPassword(password);
                    user.passwordEncode(passwordEncoder);
                    return userRepository.save(user);   // 비밀번호 업데이트
                });
    }

    /**
     * 이메일을 이용하여 일반 회원 조회
     * @param email 이메일
     * @return 조회된 회원
     */
    public Optional<User> findByEmailAndSocialTypeIsNull(String email) {
        return userRepository.findByEmailAndSocialTypeIsNull(email);
    }

    /**
     * 닉네임을 이용하여 회원 조회
     * @param nickname 닉네임
     * @return 조회된 회원
     */
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }


    /**
     * SecurityContextHolder > UserDetails에서 User 조회
     * 비회원이라도 예외처리를 발생 시키지 않고 null 값을 반환한다.
     *
     * 기존 getCurrentUser 는 unCheckedException 를 발생시켰는데
     * try-catch 문으로 예외처리를 하더라도 rollback 되버리는 현상이 발생한다
     * Transactional(readOnly = true) 해도 마찬가지이다.
     * 그리고 데이터를 받을 수 없게 된다
     *
     * 비회원이 접근 할 수 있는 API 에서는 null 값으로 처리 하여서
     * 회원인지 비회원인지 구분하자
     *
     * @return
     */
    public User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority
        ).findFirst().orElse(null);

        if (role == null || role.equals(Role.ANONYMOUS.getAuthority())) {
            return null;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        return findByEmail(email).orElse(null);
    }


    /**
     * SecurityContextHolder > UserDetails에서 User 조회
     * @return
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();
        String email = null;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        if (email != null) {
            return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("error.notfound.user"));
        } else {
            throw new ResourceNotFoundException("error.notfound.user");
        }
    }


}
