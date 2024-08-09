package com.mungwithme.security.jwt.filter;

import com.mungwithme.security.jwt.PasswordUtil;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 쿠키에 AccessToken만 담아서 요청
 * 클라이언트에서 확인 후 AccessToken 만료 시에만 RefreshToken을 요청 쿠키에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
 *                              인증 성공 처리는 하지 않고 실패 처리
 *
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면 아래 과정 생략
            return;
        }

        // 사용자 요청 쿠키에서 RefreshToken 추출
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)   // refresh Token이 있고 검증되면 반환
                .orElse(null);                // 없으면 null 반환


        if (refreshToken != null) { // refresh Token이 있으면 AccessToken 이 만료되었다는 것
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken); // 해당 refresh Token을 가지고 있는 회원이 있는 찾고, 있으면 Refresh/Access 토큰 모두 재발행
            return; // RefreshToken을 보낸 경우에는 AccessToken을 재발급 하고 인증 처리는 하지 않게 하기위해 바로 return으로 필터 진행 막기
        }

        if (refreshToken == null) { // refresh Token이 없으면 AccessToken을 검증하여 인증처리
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /**
     * Refresh Token 으로 유저 정보 찾기 & Access/Refresh Token 재발급 메소드
     * @param refreshToken Access Token이 만료 되어서 새로 발급하기 위해 이용
     */


    /** @param  -> createAccessToken 매개변수 role값 추가
     * @modification.author 전형근
     * @modification.date 2024.8.9
     * @modifiation.details 토큰 생성 메서드의 role값 받는것을 추가.
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken) // refresh Token으로 회원 찾기
                .ifPresent(user -> {                    // 회원이 있다면
                    String reIssuedRefreshToken = reIssueRefreshToken(user); // refresh Token 재발행
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail(),user.getRole()), // 재발행한 Token 담아 보내기
                            reIssuedRefreshToken);
                });
    }

    /**
     * Refresh Token 생성 및 저장
     * @param user 토근 소유자
     * @return
     */
    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    /**
     * Access Token 체크 & 인증 처리 메소드
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

        jwtService.extractAccessToken(request)      // Access Token 추출
                .filter(jwtService::isTokenValid)   // 검증
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)  // 검증되면 Email(Claim) 추출
                        .ifPresent(email -> userRepository.findByEmail(email)   // 추출된 이메일로 회원 찾기
                                .ifPresent(this::saveAuthentication)));         // 찾은 회원에 인증 허가

        filterChain.doFilter(request, response);    // 다음 인증 필터로 진행
    }

    /**
     * 인증 허가 메소드
     * @param myUser
     */
    public void saveAuthentication(User myUser) {
        String password = myUser.getPassword(); // 회원 비밀번호
        if (password == null) {                 // 비밀번호가 null(소셜회원일 경우)이면 임의 설정
            password = PasswordUtil.generateRandomPassword();
        }

        // 유저 정보
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(password)
                .roles(myUser.getRole().name())
                .build();

        // 허가된 인증 정보
        // 파라미너 : 유저 정보, 비밀번호, 권한 목록
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        // SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
        // setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}