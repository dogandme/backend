package com.mungwithme.user.service;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.common.exception.DuplicateResourceException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.util.RegexPatterns;
import com.mungwithme.common.util.TokenUtils;
import com.mungwithme.likes.service.LikesService;
import com.mungwithme.marking.service.marking.MarkingService;
import com.mungwithme.pet.service.PetService;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.OAuth2UserInfo;
import com.mungwithme.security.oauth.service.OAuth2Service;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.model.SocialType;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.dto.request.UserDeleteDto;
import com.mungwithme.user.model.dto.request.UserPwUpdateDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;
    private final MarkingService markingService;
    private final LikesService likesService;


    private final PetService petService;
    private final JwtService jwtService;

    private final UserFollowService userFollowService;

    private final OAuth2Service oAuth2Service;


    private static final String BEARER = "Bearer_";

    /**
     * 화원가입 및 NONE권한 토큰 발행
     *
     * @param userSignUpDto
     *     가입요청 회원정보
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
     *
     * @param userSignUpDto
     *     추가 회원정보
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
     *
     * @param email
     *     이메일
     * @param password
     *     신규 비밀번호
     */
    @Transactional
    public void editPasswordByEmail(String email, String password) {
        userQueryService.findByEmail(email) // 이메일을 이용하여 회원 조회
            .ifPresent(user -> user.updatePw(password, passwordEncoder));
    }

    /**
     * 비밀번호 변경 API
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
        currentUser.updatePw(newPw, passwordEncoder);
    }

    /**
     * 회원 탈퇴 API
     * <p>
     * <p>
     * 펫 삭제
     * <p>
     * address 삭제
     * 마킹 삭제
     * 저장 삭제
     * 좋아요 삭제
     * 팔로우 삭제
     * 주소 삭제
     */
    @Transactional
    public void removeUser(UserDeleteDto userDeleteDto) {

        // 탈퇴 할 유저
        User currentUser = userQueryService.findCurrentUser();

        // 만약 유저가 oAuthAPI 를 연동한 회원일 경우
        // 연동 해제
        if (currentUser.getSocialType() != null) {
            oAuth2Service.disconnectOAuth2Account(currentUser.getSocialType(), currentUser.getOAuthRefreshToken());
        } else {

            String password = userDeleteDto.getPassword();
            // 빈값인지 확인
            if (!StringUtils.hasText(password)) {
                throw new IllegalArgumentException("error.arg.pw");
            }

            boolean matches = passwordEncoder.matches(password, currentUser.getPassword());
            // 일치하지 않으면 예외 발생
            if (!matches) {
                throw new IllegalArgumentException("error.arg.auth.pw");
            }

        }

        // marking 에 관련된 모든걸 삭제
        markingService.removeAllMarkingsByUser(currentUser);

        // 좋아요 전부 삭제
        likesService.removeAllByUser(currentUser);

        // 팔로우 삭제
        userFollowService.removeAllByUser(currentUser);

        //pet 삭제
        petService.deletePet(currentUser);

        // 유저 삭제
        removeUser(currentUser);
    }


    /**
     * token 업데이트 및 response 저장
     *
     * @param email
     * @param refreshToken
     * @param oAuthAccessToken
     * @param finalOAuthRefreshToken
     */
    @Transactional
    public void editRefreshTokenAndSetCookie(String email, String refreshToken, String oAuthAccessToken,
        String finalOAuthRefreshToken, HttpServletResponse response, int maxAge) {
        userQueryService.findByEmail(email).ifPresent(currentUser ->
            {
                currentUser.updateRefreshToken(refreshToken);
                currentUser.updateOauthAccessToken(oAuthAccessToken);
                currentUser.updateOauthRefreshToken(finalOAuthRefreshToken);

                if (currentUser.getNickname() != null) {
                    Cookie nicknameCookie = new Cookie("nickname", currentUser.getNickname());
                    nicknameCookie.setPath("/login");
                    nicknameCookie.setMaxAge(maxAge);
                    response.addCookie(nicknameCookie);
                }

            }
        );

    }


    @Transactional
    public void removeUser(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User getOrSave(OAuth2UserInfo oAuth2UserInfo) {

        User user = userQueryService.findByEmail(oAuth2UserInfo.email()).orElse(null);

        if (user == null) {
            User newUser = oAuth2UserInfo.toEntity();
            addUser(newUser);
            return newUser;
        }
        return user;
    }


}
