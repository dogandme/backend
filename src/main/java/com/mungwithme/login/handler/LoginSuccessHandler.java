package com.mungwithme.login.handler;

import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.Role;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON 로그인 성공 시 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;


    /**

//   * @param 토큰 role값 검증을 위함
     * @modification.author 전형근
     * @modification.date 2024.8.8
     * @modifiation.details role값을 뽑아내서 토큰생성에 대입.
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String email = extractUsername(authentication);             // 인증 정보에서 Username(email) 추출 (JwtAuthenticationProcessingFilter에서 생성했었음)

        List<String> roles = extractRoles(authentication);          // 인증 정보에서 역할(Role) 추출

//        Role값 추가로 인한 createAccessToken 매개변수,메서드 변경
        String accessToken = jwtService.createAccessToken(email, Role.valueOf(roles.get(0)));   // AccessToken 발급
        String refreshToken = jwtService.createRefreshToken();      // RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 쿠키에 AccessToken, RefreshToken 담아 응답

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);  // sendAccessAndRefreshToken에서는 발급만 함으로 refresh Token 저장 필요
                });

        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
    }

    /**
     * 인증정보에서 username(email) 추출
     * @param authentication 인증 정보
     */
    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    /**
     * 인증정보에서 역할(Role) 추출
     * @param authentication 인증 정보
     * @return 역할 목록
     */
    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
