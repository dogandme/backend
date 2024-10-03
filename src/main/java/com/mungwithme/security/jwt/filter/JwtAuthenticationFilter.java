package com.mungwithme.security.jwt.filter;

import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.enums.Role;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 사용자 요청 쿠키에서 accessToken 추출
        // accessToken이 유효하지 않으면 해당 상태코드 전송
        String accessToken = jwtService.extractAccessToken(request)
            .filter(jwtService::isTokenValid)   // refresh Token이 있고 검증되면 반환
            .orElse(null);                // 없으면 null 반환

        if (accessToken != null) {
            // AccessToken이 유효하면 검증하여 인증처리
            checkAccessTokenAndAuthentication(request);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Access Token 체크 & 인증 처리 메소드
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request){

        // 검증
        // 검증되면 PayLoad 에 있는 값을 Map<String,Claim>으로 반환
        jwtService.extractAccessToken(request)      // Access Token 추출
            .filter(jwtService::isTokenValid).flatMap(jwtService::getJwtClaim).ifPresent(claim ->
                saveAuthentication(
                    // email 추출
                    claim.get(JwtService.EMAIL_CLAIM).asString(),
                    // role 추출
                    Role.findByStr(claim.get(JwtService.ROLE_CLAIM).asString()).name()
                ));         // 찾은 회원에 인증 허가
    }
    /**
     * 인증 허가 메소드
     * @param email
     * @param role
     */
    public void saveAuthentication(String email,String role) {
        /**
         * 이메일 및 사용권한
         */

        // 유저 정보
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
            .username(email)
            .roles(role)
            .password("")
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