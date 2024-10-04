package com.mungwithme.login.model.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.mungwithme.login.model.dto.DeviceDetails;
import com.mungwithme.login.model.dto.UserLocationDto;
import com.mungwithme.login.model.enums.UserAgent;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@Getter
@Setter(value = PRIVATE)
@Embeddable
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
public class DefaultLocationLog {

    private String ip;
    // 로그인시도 국가
    private String countryName;

    // 접속한 기기
    @Enumerated(STRING)
    private UserAgent userAgent;
    //위도
    private String latitude;

    // 경도
    private String longitude;

    // 상태값 True면 활성화 , False면 비활성화
    private Boolean isStatus;

    @Builder
    public DefaultLocationLog(String ip, String countryName, UserAgent userAgent, String latitude, String longitude,
        Boolean isStatus) {
        this.ip = ip;
        this.countryName = countryName;
        this.userAgent = userAgent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isStatus = isStatus;
    }

    public static DefaultLocationLog createDefaultLocationLog(boolean isStatus,
        UserLocationDto userLocationDto,
        String userAgent) {

        return DefaultLocationLog.builder()
            .ip(userLocationDto.getIpAddress()) //ip 저장
            .countryName(userLocationDto.getCountryName()) // iso Code 저장
            .latitude(userLocationDto.getLatitude()) // 위도
            .longitude(userLocationDto.getLongitude()) // 경도
            .userAgent(DeviceDetails.getUserAgent(userAgent)) // 기기 저장
            .isStatus(isStatus).build();
    }


    public void changeIsStatus(Boolean isStatus) {
        this.setIsStatus(isStatus);
    }


}
