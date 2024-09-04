package com.mungwithme.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.dto.CustomUserDetails;
import com.mungwithme.user.model.entity.User;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON 로그인 성공 시 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class CustomJsonAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BaseResponse baseResponse;
    private final ObjectMapper objectMapper;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;


    /**
     * 로그인 성공 시 호출
     * @modification.author 전형근
     * @modification.date 2024.8.8
     * @modification.details role값을 뽑아내서 토큰생성에 대입.
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        HashMap<String, Object> result = new HashMap<>();

        String email = extractUsername(authentication);             // 인증 정보에서 Username(email) 추출 (JwtAuthenticationProcessingFilter에서 생성했었음)
        List<String> roles = extractRoles(authentication);          // 인증 정보에서 역할(Role) 추출
        Long userId = extractUserId(authentication);                // 인증 정보에서 userId(PK) 추출

        // Role값 추가로 인한 createAccessToken 매개변수,메서드 변경
        String accessToken = jwtService.createAccessToken(email, roles.get(0), userId);   // AccessToken 발급
        String refreshToken = jwtService.createRefreshToken();                            // RefreshToken 발급
        jwtService.setRefreshTokenCookie(response, refreshToken); // 응답 쿠키에 AccessToken, RefreshToken 담아 응답 Todo 수정 필요

        // 새로 발급된 refresh token 저장
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    result.put("nickname", user.getNickname());
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });

        result.put("authorization", accessToken);
        result.put("role", roles.get(0));
        result.put("userId", userId);

        baseResponse.sendContentResponse(result, response, 200, objectMapper);  // 권한에 따른 분기처리를 위해 role 추가하여 성공 응답

        log.info("로그인에 성공하였습니다. 이메일 : {} / AccessToken : {} ", email, accessToken);
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

    /**
     * 인증정보에서 userId(PK) 추출
     */
    private Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
}
