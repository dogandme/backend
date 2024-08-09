package com.mungwithme.security.jwt.filter;


import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

/**
 * 필터이기때문에
 * post logout만 로직을 타고
 * 나머지 경로는 pass
 */

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);


    }


//    /logout post에만 반응한다.
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {


        //path and method verify
//       필터이기 때문에 모든 요청이 거쳐간다. 로그아웃 요청만 획득하고 나머진 패스
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

//        POST요청의 로그아웃이 아니면 패스한다.
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("Authorization-refresh")) {

                refreshToken = cookie.getValue();
                System.out.println("refreshToken = " + refreshToken);
            }
        }

        //refresh null check
        if (refreshToken == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        //expired check
            if(!jwtService.isTokenValid(refreshToken)){

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;

            };



        //DB에 저장되어 있는지 확인
        Optional<User> byRefreshToken = userRepository.findByRefreshToken(refreshToken);

        Boolean isExist = byRefreshToken.isPresent();
        if (!isExist) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        User user = byRefreshToken.get();
        user.updateRefreshToken("null");
        userRepository.save(user);


        //Refresh 토큰 Cookie 값 0
        Cookie refresh = new Cookie("Authorization-refresh", null);
        refresh.setMaxAge(0);
        refresh.setPath("/");

        Cookie access = new Cookie("Authorization", null);
        refresh.setMaxAge(0);
        refresh.setPath("/");


        response.addCookie(refresh);
        response.addCookie(access);
        response.setStatus(HttpServletResponse.SC_OK);



    }
}
