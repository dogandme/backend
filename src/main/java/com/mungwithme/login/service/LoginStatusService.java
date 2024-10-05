package com.mungwithme.login.service;


import com.auth0.jwt.interfaces.Claim;
import com.mungwithme.common.exception.UnauthorizedException;
import com.mungwithme.common.redis.RedisUtil;
import com.mungwithme.login.model.entity.LoginStatus;
import com.mungwithme.login.repository.LoginStatusRepository;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자가 로그인 성공한
 * <p>
 * 정보를 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginStatusService {

    private final LoginStatusRepository loginStatusRepository;
    private final LocationFinderService locationFinderService;
    private final RedisUtil redisUtil;

    /**
     * 로그인 시
     * 로그인 아이디 관리를 위해
     * 저장
     *
     * 3개의 세션 제한
     * 3개 이상시 가장 오래된 세션 로그아웃
     *
     * @param userAgent
     * @param refreshToken
     * @param userId
     * @param sessionId
     */
    @Transactional
    public void addStatus(String userAgent, String refreshToken, String redisAuthToken, long userId, String sessionId) {
        if (userId == 0) {
            throw new IllegalArgumentException("error.arg");
        }

        User user = User.builder().id(userId).build();

        // UserLoginStatus 생성 후
        LoginStatus saveLoginStatus = LoginStatus.create(locationFinderService,
            userAgent,
            user, refreshToken, sessionId, redisAuthToken);

        /**
         * Status에 새로 저장하기 전에 현재 세션을 가진
         * 기기들 전부 loginStatus OFF로 바꿔줌으로 인해서
         * 겹치는 문제 제거
         */
        List<LoginStatus> findStatus = loginStatusRepository.
            findList(user, true, true);

        Set<String> statusSet = findStatus.stream().map(LoginStatus::getSessionId).collect(Collectors.toSet());

        // 현재 로그인된 디바이스가 3개 이상이면
        if (statusSet.size() >= 3) {
            // 로그인 시간 기준으로 오래된 순서로 정렬
            findStatus.sort(Comparator.comparing(LoginStatus::getRegDt));

            // 가장 오래된 세션을 강제로 로그아웃
            LoginStatus oldLoginStatus = findStatus.get(0);
            List<LoginStatus> logoutStatus = findStatus.stream()
                .filter(status -> status.getSessionId().equals(oldLoginStatus.getSessionId()))
                .collect(Collectors.toList());

            // 해당 세션의 상태를 비활성화
            editStatus(user, false, false, oldLoginStatus.getSessionId());

            // Redis에서 해당 세션의 모든 토큰 삭제
            removeAllByRedisToken(logoutStatus);
        }

        // 현재 세션 ID와 동일한 모든 기존 상태를 로그아웃 처리
        List<LoginStatus> currentSessionStatus = findStatus.stream()
            .filter(status -> status.getSessionId().equals(sessionId)).toList();

        if (!currentSessionStatus.isEmpty()) {
            editStatus(user, false, false, sessionId);
            // redis token 다 삭제
            removeAllByRedisToken(currentSessionStatus);
        }
        //새롭게 저장
        loginStatusRepository.save(saveLoginStatus);
    }

    private void removeAllByRedisToken(List<LoginStatus> logoutStatus) {
        for (LoginStatus status : logoutStatus) {
            if (redisUtil.hasRedis(status.getRedisAuthToken())) {
                redisUtil.deleteData(status.getRedisAuthToken());
            }
        }
    }


    /**
     * status 변경
     * @param user
     * @param loginStatus
     * @param isStatus
     * @param sessionId
     */
    @Transactional
    public void editStatus(User user, boolean loginStatus, boolean isStatus, String sessionId) {
        loginStatusRepository.update(user, loginStatus, isStatus, sessionId);
    }






    /**
     * sessionId에 해당하는 status 를 전부 다 off
     *
     * @param user
     * @param sessionId
     */
    @Transactional
    public void logoutStatus(User user,String sessionId) {

        List<LoginStatus> statusList = loginStatusRepository.findList(user, true, true, sessionId);

        removeAllByRedisToken(statusList);
        editStatus(user, false, false, sessionId);

    }


    /**
     * 유저 해당하는 status 를 전부 다 삭제 및 redis 정리
     *
     * @param user
     */
    @Transactional
    public void removeAllStatusWithRedis(User user) {

        // 활성화 된 status 검색 후
        List<LoginStatus> statusList = loginStatusRepository.findList(user, true, true);

        // redis 전부삭제
        removeAllByRedisToken(statusList);

        // delete 삭제
        removeAllStatusByUser(user);

    }

    /**
     *
     *  유저 해당하는 status 를 전부 다 삭제
     * @param user
     */
    @Transactional
    public void removeAllStatusByUser(User user) {
        loginStatusRepository.removeAllByUser(user);
    }

}
