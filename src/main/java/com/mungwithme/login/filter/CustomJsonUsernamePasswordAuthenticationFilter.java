package com.mungwithme.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 로그인 요청을 처리하는 커스텀 필터
 */
@Slf4j
public class CustomJsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login"; // "/login"으로 오는 요청을 처리
    private static final String HTTP_METHOD = "POST";                 // 로그인 HTTP 메소드는 POST
    private static final String CONTENT_TYPE = "application/json";    // JSON 타입의 데이터로 오는 로그인 요청만 처리
    private static final String USERNAME_KEY = "email";               // 회원 로그인 시 이메일 요청 JSON Key : "email"
    private static final String PASSWORD_KEY = "password";            // 회원 로그인 시 비밀번호 요청 JSon Key : "password"
    private static final String PERSIST_LOGIN = "persistLogin";      // 회원 로그인 시 로그인 유지 여부
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
        new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // "/login" + POST로 온 요청에 매칭된다.

    private final ObjectMapper objectMapper;

    // JSON 형식의 로그인 요청을 처리하도록 커스텀
    public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER); // 위에서 설정한 "login" + POST로 온 요청을 처리하기 위해 설정
        this.objectMapper = objectMapper;
    }

    /**
     * 인증 처리 메소드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException {

        // 요청 ContentType이 올바르지 않을 경우
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException(
                "Authentication Content-Type not supported: " + request.getContentType());
        }

        // request에서 messageBody(JSON) 반환
        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        // messageBody를 Map으로 변환
        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
        String email = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);
        ;
        Boolean persistLogin = Boolean.valueOf(String.valueOf(usernamePasswordMap.get(PERSIST_LOGIN)));

        //principal(username), credentials(password) 전달
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        // 인증 처리
        Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

        // 인증 성공 후 persistLogin 저장
//        userRepository.findByEmail(email).ifPresent(user -> {
//            user.updatePersistLogin(persistLogin); // persistLogin 업데이트
//            userRepository.save(user); // 변경 사항 저장
//        });


        request.setAttribute(PERSIST_LOGIN, persistLogin);
        return authentication;
    }
}