package com.mungwithme.security.jwt.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

/**
 * 필터이기때문에
 * post logout만 로직을 타고
 * 나머지 경로는 pass
 */

@Slf4j
@RequiredArgsConstructor
public class JwtLogoutFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BaseResponse baseResponse;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // ServletRequest와 ServletResponse를 HttpServletRequest와 HttpServletResponse로 캐스팅
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // POST "/logout"이 아니면 CustomLogoutFilter 패스
        if (!"/logout".equals(request.getRequestURI()) || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 검증
        Optional<String> refreshToken = jwtService.extractRefreshToken(request);
        if (refreshToken.isEmpty() || !jwtService.isTokenValid(refreshToken.get())) {
            baseResponse.handleResponse(response, baseResponse.sendErrorResponse(401, "로그아웃 토큰 검증 실패"));
            return;
        }

        // refreshToken으로 User 조회
        Optional<User> byRefreshToken = userRepository.findByRefreshToken(refreshToken.get());
        if (byRefreshToken.isEmpty()) {
            baseResponse.handleResponse(response, baseResponse.sendErrorResponse(404, "유저 조회 실패"));   // 조회된 User가 없을 경우 에러
            return;
        }

        //로그아웃 진행
        jwtService.updateRefreshToken(byRefreshToken.get().getEmail(), null); // Refresh 토큰 DB에서 제거
        jwtService.clearAllCookie(request, response);                                    // 토큰 삭제
        baseResponse.handleResponse(response, baseResponse.sendSuccessResponse(200));      // 로그아웃 성공 응답
    }
}
