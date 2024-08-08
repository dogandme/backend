package com.mungwithme.security.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private int accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationPeriod;

    @Value("${jwt.access.cookie}")
    private String accessCookie;

    @Value("${jwt.refresh.cookie}")
    private String refreshCookie;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer";

    private final UserRepository userRepository;

    /**
     * AccessToken 생성
     * @param email Claim 추가를 위함
     * @return
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return JWT.create()                             // JWT 토큰을 생성하는 빌더
                .withSubject(ACCESS_TOKEN_SUBJECT)      // JWT의 Subject 지정
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정
                .withClaim(EMAIL_CLAIM, email)          // email Claim 설정
                .sign(Algorithm.HMAC512(secretKey));    // HMAC512 알고리즘 사용, application-jwt.yml에서 지정한 secret 키로 암호화
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * AccessToken 쿠키에 담아 보내기
     * @param accessToken 쿠키에 담을 토큰
     *
     * @modification.author 장수현
     * @modification.date 2024.8.8
     * @modifiation.details 토큰 헤더 전송 -> 토큰 쿠키 전송
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);         // 요청이 성공적으로 처리되었음을 나타냄(HTTP 상태코드 200)
        response.addCookie(createCookie(accessCookie, accessToken, accessTokenExpirationPeriod, false));

        log.info("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken + RefreshToken 쿠키에 담아 보내기
     * @param accessToken 쿠키에 담을 토큰
     * @param refreshToken 쿠키에 담을 토큰
     *
     * @modification.author 장수현
     * @modification.date 2024.8.8
     * @modifiation.details 토큰 헤더 전송 -> 토큰 쿠키 전송
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenCookie(response, accessToken);
        setRefreshTokenCookie(response, refreshToken);

        log.info("Access Token, Refresh Token 쿠키 설정 완료");
    }

    /**
     * 토큰에서 RefreshToken 추출
     *
     * @modification.author 장수현
     * @modification.date 2024.8.8
     * @modifiation.details 헤더 토큰 추출 -> 쿠키 토큰 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())                                    // 쿠키 배열을 가져옵니다.
                .flatMap(cookies -> Arrays.stream(cookies)                                  // 쿠키 배열을 스트림으로 변환합니다.
                        .filter(cookie -> refreshCookie.equals(cookie.getName()))           // refreshToken 쿠키를 찾습니다.
                        .map(Cookie::getValue)                                              // 쿠키의 값을 가져옵니다.
                        .filter(refreshToken -> refreshToken.startsWith(BEARER))            // 리프레시 토큰이 "BEARER "로 시작하면
                        .map(refreshToken -> refreshToken.replace(BEARER, ""))    // "BEARER "를 제거하고 추출합니다.
                        .findFirst());
    }

    /**
     * 토큰에서 AccessToken 추출
     *
     * @modification.author 장수현
     * @modification.date 2024.8.8
     * @modifiation.details 헤더 토큰 추출 -> 쿠키 토큰 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())                                    // 쿠키 배열을 가져옵니다.
                .flatMap(cookies -> Arrays.stream(cookies)                                  // 쿠키 배열을 스트림으로 변환합니다.
                        .filter(cookie -> accessCookie.equals(cookie.getName()))            // accessToken 쿠키를 찾습니다.
                        .map(Cookie::getValue)                                              // 쿠키의 값을 가져옵니다.
                        .filter(refreshToken -> refreshToken.startsWith(BEARER))            // 리프레시 토큰이 "BEARER "로 시작하면
                        .map(refreshToken -> refreshToken.replace(BEARER, ""))    // "BEARER "를 제거하고 추출합니다.
                        .findFirst());
    }

    /**
     * AccessToken에서 Email 추출
     * @param accessToken
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))    // 토큰 검증기 생성
                    .build()
                    .verify(accessToken)    // accessToken을 검증하고 유효하지 않다면 예외 발생
                    .getClaim(EMAIL_CLAIM)  // claim(Emial) 가져오기
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * AccessToken 쿠키 설정
     *
     * @modification.author 장수현
     * @modification.date 2024.8.8
     * @modification.details 토큰 헤더 저장 -> 토큰 쿠키 저장
     */
    public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        response.addCookie(createCookie(accessCookie, accessToken, accessTokenExpirationPeriod, false));
    }

    /**
     * RefreshToken 쿠키 설정
     *
     * @modification.author 장수현
     * @modification.date 2024.8.8
     * @modification.details 토큰 헤더 저장 -> 토큰 쿠키 저장
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        response.addCookie(createCookie(refreshCookie, refreshToken, refreshTokenExpirationPeriod, true));
    }

    /**
     * 쿠키 생성
     * @param key 쿠키명
     * @param value 토큰
     * @param expirationPeriod 토큰 만료 시간
     * @param httpOnly httpOnly 설정 여부
     */
    public Cookie createCookie(String key, String value, int expirationPeriod, boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expirationPeriod);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)   // 이메일로 회원 찾기
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),  // 있으면 리프레시 토큰 업데이트
                        () -> new Exception("일치하는 회원이 없습니다.")     // 없으면 예외발생
                );
    }

    /**
     * 토큰 검증
     * @param token 검증할 토큰
     */
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
