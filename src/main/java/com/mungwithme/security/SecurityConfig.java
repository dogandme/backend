package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.mungwithme.login.handler.LoginFailureHandler;
import com.mungwithme.login.handler.LoginSuccessHandler;
import com.mungwithme.login.service.LoginService;
import com.mungwithme.security.jwt.filter.CustomLogoutFilter;
import com.mungwithme.security.jwt.filter.JwtAuthenticationProcessingFilter;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.handler.CustomFailureHandler;
import com.mungwithme.security.oauth.handler.CustomSuccessHandler;
import com.mungwithme.security.oauth.service.CustomOAuth2UserService;
import com.mungwithme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())               // 없으면 API 테스트 불가. Cross-Site Request Forgery 보호 기능을 비활성화, REST API는 CSRF 공격에 대한 보호가 필요 없음. 대신 JWT/OAuth2와 같은 다른 인증 방법 사용. Todo REST API는 왜 필요없는지 더 자세한 공부 필요
                .formLogin(AbstractHttpConfigurer::disable) // FormLogin 사용 X
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 인증(이름과 비밀번호를 Base64로 인코딩하여 HTTP 헤더에 포함시켜 서버에 전달하는 인증 방식) 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않으므로 STATELESS로 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/users", "/oauth/**").permitAll()
                                .anyRequest().authenticated());

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler)
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                            .userService(customOAuth2UserService)))
                ;

        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

        //logout
        http.addFilterBefore(new CustomLogoutFilter(jwtService, userRepository), LogoutFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder를 자동으로 주입하기 위해 빈으로 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정 후 등록
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // PasswordEncoder를 사용
        provider.setUserDetailsService(loginService);   // UserDetailsService는 커스텀 LoginService로 등록
        return new ProviderManager(provider);
    }

    /**
     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
     */
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRepository);
    }

    /**
     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
     */
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    /**
     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
     */
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());        // 위에서 등록한 AuthenticationManager(ProviderManager) 설정
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());   // 로그인 성공 시 호출할 handler
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());   // 실패 시 호출할 handler
        return customJsonUsernamePasswordLoginFilter;
    }

    /**
     * JwtAuthenticationProcessingFilter 빈 등록
     */
    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);
        return jwtAuthenticationFilter;
    }
}
