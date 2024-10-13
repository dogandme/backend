package com.mungwithme.user.service;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.common.email.EmailService;
import com.mungwithme.common.exception.CustomIllegalArgumentException;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.redis.model.RedisKeys;
import com.mungwithme.common.util.TokenUtils;
import com.mungwithme.likes.service.LikesService;
import com.mungwithme.login.service.LoginStatusService;
import com.mungwithme.marking.service.marking.MarkingService;
import com.mungwithme.pet.service.PetService;
import com.mungwithme.security.jwt.PasswordUtil;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.OAuth2UserInfo;
import com.mungwithme.security.oauth.service.OAuth2Service;

import com.mungwithme.user.model.entity.UserAddress;
import com.mungwithme.user.model.dto.request.*;

import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.model.dto.UserResponseDto;
import com.mungwithme.user.model.dto.UserSignUpDto;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.enums.SocialType;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AddressQueryService addressQueryService;

    private final PasswordEncoder passwordEncoder;
    private final UserQueryService userQueryService;
    private final MarkingService markingService;
    private final LikesService likesService;


    private final PetService petService;
    private final JwtService jwtService;

    private final UserFollowService userFollowService;

    private final OAuth2Service oAuth2Service;

    private final LoginStatusService loginStatusService;

    private final UserAddressService userAddressService;

    private final EmailService emailService;


    private static final String BEARER = "Bearer_";

    /**
     * 화원가입 및 NONE권한 토큰 발행
     *
     * @param userSignUpDto
     *     가입요청 회원정보
     */
    @Transactional
    public UserResponseDto signUp(UserSignUpDto userSignUpDto, HttpServletResponse response, HttpServletRequest request)
        throws Exception {

        UserResponseDto userResponseDto = new UserResponseDto();

        // 이메일 중복 확인
        userQueryService.duplicateEmail(userSignUpDto.getEmail());

        User newUser = User.builder()
            .token(TokenUtils.getToken())
            .email(userSignUpDto.getEmail())
            .password(userSignUpDto.getPassword())
            .role(Role.NONE)
            .build();

        // NONE 권한의 토큰 발행(기본정보입력 화면으로 넘어가기 위함)
        String email = newUser.getEmail();
        Role role = newUser.getRole();

        String redisAuthToken = TokenUtils.getRedisAuthToken();

        String accessToken = jwtService.createAccessToken(email, role.getKey(), redisAuthToken);   // AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(email, role.getKey(),           // RefreshToken 발급
            redisAuthToken);

        jwtService.setRefreshTokenCookie(response, refreshToken);                          // RefreshToken 쿠키에 저장

        userResponseDto.setAuthorization(accessToken);
        userResponseDto.setRole(role.getKey());

        // 유저 등록
        newUser.passwordEncode(passwordEncoder);   // 비밀번호 암호화
        newUser.setSocialType(SocialType.EMAIL);   // 이메일 회원가입 시
        addUser(newUser);    // DB 저장

        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession().getId();

        loginStatusService.addStatus(userAgent, refreshToken, RedisKeys.REDIS_AUTH_TOKEN_LOGIN_KEY + redisAuthToken,
            newUser.getId(), sessionId);

        return userResponseDto;
    }

    /**
     * 추가 정보 저장 및 GUEST권한 토큰 발행
     *
     * @param userSignUpDto
     *     추가 회원정보
     */
    @Transactional
    public UserResponseDto signUp2(UserSignUpDto userSignUpDto, HttpServletRequest request) throws Exception {

        // 닉네임 중복 확인
        userQueryService.duplicateNickname(userSignUpDto.getNickname());

        // 유저 정보 확인
        User currentUser = userQueryService.findCurrentUser();

        // 요청 데이터에서 region ID 리스트를 가져옴
        List<Long> regionIds = userSignUpDto.getRegion();

        // region ID를 기반으로 Address 엔터티 조회
        Set<Address> addresses = regionIds.stream()
            .map(addressId -> addressQueryService.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("error.notfound.address")))
            .collect(Collectors.toSet());

        Set<UserAddress> userAddresses = addresses.stream().map(address -> UserAddress.create(currentUser, address))
            .collect(Collectors.toSet());

        currentUser.setRole(Role.GUEST);
        currentUser.setNickname(userSignUpDto.getNickname());
        currentUser.setGender(userSignUpDto.getGender());
        currentUser.setAge(userSignUpDto.getAge());
        currentUser.setUserAddresses(userAddresses);
        currentUser.setMarketingYn(userSignUpDto.getMarketingYn());
        currentUser.setNickLastModDt(LocalDateTime.now());

        UserResponseDto userResponseDto = new UserResponseDto();

        // refreshToken 에서 RedisToken 추출
        String redisAuthToken = jwtService.getRedisAuthToken(request);

        String accessToken = jwtService.createAccessToken(currentUser.getEmail(), currentUser.getRole().getKey(),
            redisAuthToken);
        userResponseDto.setAuthorization(accessToken);
        userResponseDto.setRole(currentUser.getRole().getKey());
        userResponseDto.setNickname(currentUser.getNickname());
        return userResponseDto;
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

        // 패스워드가 없고 일반회원이 아닌 경우
        if (!StringUtils.hasText(currentUser.getPassword()) && currentUser.getSocialType() != SocialType.EMAIL) {
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
     * 닉네임 변경 API
     *
     * @param userNicknameUpdateDto
     */
    @Transactional
    public void editNickname(UserNicknameUpdateDto userNicknameUpdateDto) {
        User currentUser = userQueryService.findCurrentUser();

        if (currentUser.getNickname().equals(userNicknameUpdateDto.getNickname())) {
            return;
        }

        // 최신 변경 일자
        LocalDateTime nickLastModDt = currentUser.getNickLastModDt();

        // 닉네임 변경 가능 시작 시간
        LocalDateTime plusMonths = nickLastModDt.plusMonths(1);

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();


        String nickname = userNicknameUpdateDto.getNickname();

        // 사용자가 닉네임을 변경을 한지 한달을 초과했는지 획인을 위해
        // 현재 시간을 가지고 와서 DB에 저장되어 있는 날짜와 비교 후 초과가 되지 않았다면 예외 발생
        // 가입하고나서 처음 닉네임을 변경한다면 변경 가능
        if (nickLastModDt != null && !now.isAfter(plusMonths)) {
            // 1개월이 넘지못했다면
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd E HH:mm");
            throw new CustomIllegalArgumentException("error.arg.nickname.ex", plusMonths.format(dateTimeFormatter));
        }
        // 중복 검사
        userQueryService.duplicateNickname(nickname);

        // 닉네임 변경 시간을 저장
        currentUser.updateNickModDt(now);
        // Update
        currentUser.updateNickname(userNicknameUpdateDto.getNickname());
    }

    /**
     * 성별 업데이트 API
     *
     * @param userGenderUpdateDto
     */
    @Transactional
    public void editGender(UserGenderUpdateDto userGenderUpdateDto) {
        User currentUser = userQueryService.findCurrentUser();

        if (!currentUser.getGender().equals(userGenderUpdateDto.getGender())) {
            currentUser.updateGender(userGenderUpdateDto.getGender());
        }
    }

    /**
     * 나이 업데이트 API
     *
     * @param userAgeUpdateDto
     */
    @Transactional
    public void editAge(UserAgeUpdateDto userAgeUpdateDto) {
        User currentUser = userQueryService.findCurrentUser();
        if (currentUser.getAge() != userAgeUpdateDto.getAge().getAge()) {
            currentUser.updateAge(userAgeUpdateDto.getAge());
        }
    }


    /**
     * 주소 업데이트 API
     */
    @Transactional
    public void editAddress(UserAddressUpdateDto userAddressUpdateDto) {

        // 삭제할 주소 ID 목록
        Set<Long> removeIds = userAddressUpdateDto.getRemoveIds();

        // 추가할 주소 ID 목록
        Set<Long> addIds = userAddressUpdateDto.getAddIds();

        if (removeIds.isEmpty() && addIds.isEmpty()) {
            throw new IllegalArgumentException("error.arg");
        }

        // 현재 사용자 가져오기
        User currentUser = userQueryService.findCurrentUser();

        // 사용자의 현재 주소 목록
        Set<UserAddress> userAddresses = currentUser.getUserAddresses();

        // 삭제할 Address 객체 목록
        Set<UserAddress> removeAddress = userAddresses.stream()
            .filter(address -> removeIds.contains(address.getAddress().getId()))
            .collect(Collectors.toSet());

        // 중복 주소 필터링
        Set<UserAddress> duplicateAddresses = userAddresses.stream()
            .filter(region -> addIds.contains(region.getAddress().getId()))
            .collect(Collectors.toSet());

        // 총 추가할 주소 개수에서 중복되는 주소 개수 제외
        int addSize = addIds.size() - duplicateAddresses.size();

        // 최종 주소 개수 (기존 주소 + 추가할 주소)
        int finalRegionSize = userAddresses.size() + addSize;

        // 유효성 검사: 1~5개의 주소만 허용
        if (finalRegionSize < 1 || finalRegionSize > 5) {
            throw new IllegalArgumentException("error.arg.address.limit");
        }

        // Transactional
        userAddressService.removeSet(removeAddress);

        // 중복제거
        duplicateAddresses.stream()
            .map(data -> data.getAddress().getId())
            .forEach(addIds::remove);

        // 추가할 Address 객체를 데이터베이스에서 조회
        List<Address> addressList = addressQueryService.findByIds(addIds);


        // 중복되지 않는 주소만 추가
        Set<UserAddress> addressSet = addressList.stream().map(address -> UserAddress.create(currentUser, address))
            .collect(Collectors.toSet());

        // Transactional
        // 업데이트된 주소를 사용자 객체에 설정
        userAddressService.addSet(addressSet);
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
        if (currentUser.getSocialType() != SocialType.EMAIL) {
            oAuth2Service.disconnectOAuth2Account(currentUser.getSocialType(), currentUser.getOAuthRefreshToken());
        }

        // 비밀번호가 설정이 되어 있다면 확인
        if (StringUtils.hasText(currentUser.getPassword())) {
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

        // 주소 삭제
        userAddressService.removeAllByUser(currentUser);

        //pet 삭제
        petService.deletePet(currentUser);

        // status 삭제
        loginStatusService.removeAllStatusWithRedis(currentUser);

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
        String finalOAuthRefreshToken, HttpServletResponse response, HttpServletRequest request, String redisAuthToken,
        int maxAge) {

        userQueryService.findByEmail(email).ifPresent(currentUser ->
            {
                currentUser.updateOauthAccessToken(oAuthAccessToken);
                currentUser.updateOauthRefreshToken(finalOAuthRefreshToken);
                String userAgent = request.getHeader("User-Agent");
                loginStatusService.addStatus(userAgent, refreshToken, redisAuthToken, currentUser.getId(),
                    request.getSession().getId());

                if (currentUser.getNickname() != null) {
                    Cookie nicknameCookie = new Cookie("nickname", currentUser.getNickname());
                    nicknameCookie.setPath("/login");
                    nicknameCookie.setMaxAge(maxAge);
                    response.addCookie(nicknameCookie);
                }

                addUser(currentUser);
            }
        );

    }

    @Transactional
    public void removeUser(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void editPersistLogin(User user) {

    }

    @Transactional
    public void addUser(User user) {
        userRepository.save(user);
    }

    /**
     *
     * 소셜로 회원가입시
     * get or save
     * @param oAuth2UserInfo
     * @return
     */
    @Transactional
    public User findOrSave(OAuth2UserInfo oAuth2UserInfo) {

        User user = userQueryService.findByEmail(oAuth2UserInfo.email()).orElse(null);

        if (user == null) {
            User newUser = oAuth2UserInfo.toEntity();
            addUser(newUser);
            return newUser;
        }

        // 만약 기존에 있던 계정이 일반 회원가입인 경우
        // socialType update
        if (StringUtils.hasText(user.getPassword()) && user.getSocialType() == SocialType.EMAIL) {
            user.updateSocialType(oAuth2UserInfo.socialType());
        }

        return user;
    }

    /**
     * 소셜 계정 첫 password 업데이트
     * @param socialUserPwUpdateDto 소셜 계정 비밀번호
     */
    @Transactional
    public void editSocialPassword(SocialUserPwUpdateDto socialUserPwUpdateDto) {

        // 조건1 : 소셜 계정
        User currentUser = userQueryService.findCurrentUser_v2();
        if (currentUser.getSocialType() == SocialType.EMAIL) {
            throw new IllegalArgumentException("error.arg.standard.pw");
        }

        // 조건 2 : 첫 비밀번호 설정
        if (currentUser.getPassword() != null) {
            throw new IllegalArgumentException("error.arg.standard.pw.first");
        }

        // 조건 3 : 변경 비밀번호와 변경확인 비밀번호 입력값이 동일
        String newPw = socialUserPwUpdateDto.getNewPw();
        String newPwChk = socialUserPwUpdateDto.getNewPwChk();
        if (!newPw.equals(newPwChk)) {
            throw new IllegalArgumentException("error.arg.change.pw");
        }

        // 비밀번호 업데이트
        currentUser.updatePw(newPw, passwordEncoder);
    }

    /**
     * 임시 비밀번호 이메일 전송
     * @param email 수신 이메일
     */
    @Transactional
    public void sendTemporaryPassword(String email) {

        // 1. 이메일로 일반 회원 조회 (비밀번호 미설정 소셜 회원은 임시 비밀번호 설정 불가)
        Optional<User> user = userQueryService.findByEmailAndPasswordIsNotNull(email);

        // 2. 실패 시 실패 응답 return
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("error.notfound.user.password");
        }

        // 3. 성공 시 임시 비밀번호 생성
        String temporaryPassword = PasswordUtil.generateRandomPassword();

        // 4. 임시 비밀번호 DB 업데이트
        editPasswordByEmail(email, temporaryPassword);

        // 5. 임시 비밀번호 이메일 전송
        emailService.temporaryPasswordEmail(email, temporaryPassword);
    }
}
