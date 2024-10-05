package com.mungwithme.login.model.entity;


import static jakarta.persistence.FetchType.LAZY;

import com.mungwithme.common.base.BaseTimeEntity;
import com.mungwithme.login.model.dto.DeviceDetails;
import com.mungwithme.login.model.dto.UserLocationDto;
import com.mungwithme.login.service.LocationFinderService;
import com.mungwithme.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class LoginStatus extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // refreshToken 과 accessToken이 유효한지 증명하는 token
    @Column(nullable = false)
    private String redisAuthToken;


    @Column(nullable = false,length = 1000)
    private String refreshToken;

    @Column(nullable = false)
    private String sessionId;

    //사용자 고유 번호
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //운영체제
    private String os;

    //접속한 브라우저
    private String browser;
    // 로그인 상태 값 혹은 관리
    // true:login , false: logout
    private Boolean loginStatus;

    private DefaultLocationLog defaultLog;
    @Builder
    public LoginStatus(Long id, String redisAuthToken, String refreshToken, String sessionId, User user, String os,
        String browser, Boolean loginStatus, DefaultLocationLog defaultLog) {
        this.id = id;
        this.redisAuthToken = redisAuthToken;
        this.refreshToken = refreshToken;
        this.sessionId = sessionId;
        this.user = user;
        this.os = os;
        this.browser = browser;
        this.loginStatus = loginStatus;
        this.defaultLog = defaultLog;
    }

    /**
     * 객체 생성
     *
     * @param locationFinderService
     * @param user
     * @return
     */
    public static LoginStatus create(
        LocationFinderService locationFinderService,
        String userAgent, User user, String refreshToken, String sessionId,String redisAuthToken) {
        UserLocationDto userLocationDto;
        userLocationDto = locationFinderService.findLocation();


        return LoginStatus.builder()
            .user(user)
            .loginStatus(true)
            .refreshToken(refreshToken)
            .redisAuthToken(redisAuthToken)
            .defaultLog(
                DefaultLocationLog.createDefaultLocationLog(true, userLocationDto, userAgent)
            ).sessionId(sessionId)
            .browser(DeviceDetails.getClientBrowser(userAgent))
            .os(DeviceDetails.getClientOS(userAgent)).build();
    }


}
