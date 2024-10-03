package com.mungwithme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.mungwithme.login.handler.CustomJsonAuthenticationFailureHandler;
import com.mungwithme.login.handler.CustomJsonAuthenticationSuccessHandler;
import com.mungwithme.login.service.UserDetailsServiceImpl;
import com.mungwithme.security.jwt.filter.JwtAuthenticationFilter;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.security.oauth.handler.CustomOAuthAuthenticationFailureHandler;
import com.mungwithme.security.oauth.handler.CustomOAuthAuthenticationSuccessHandler;
import com.mungwithme.security.oauth.service.CustomOAuth2UserService;
import com.mungwithme.user.model.enums.Role;
import com.mungwithme.user.repository.UserRepository;
import com.mungwithme.user.service.UserLogoutService;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // PreAuthorize를 사용하기 위해서 true로 설정한다.
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final BaseResponse baseResponse;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final CustomOAuthAuthenticationFailureHandler customOAuthAuthenticationFailureHandler;


    private final CustomLogoutHandler customLogoutHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService,UserQueryService userQueryService) throws Exception {

        http
            .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                    return configuration;
                }
            }));

        http
            .csrf(
                AbstractHttpConfigurer::disable)      // 없으면 API 테스트 불가. Cross-Site Request Forgery 보호 기능을 비활성화, REST API는 CSRF 공격에 대한 보호가 필요 없음. 대신 JWT/OAuth2와 같은 다른 인증 방법 사용. Todo REST API는 왜 필요없는지 더 자세한 공부 필요
            .formLogin(AbstractHttpConfigurer::disable) // FormLogin 사용 X
            .httpBasic(
                AbstractHttpConfigurer::disable) // httpBasic 인증(이름과 비밀번호를 Base64로 인코딩하여 HTTP 헤더에 포함시켜 서버에 전달하는 인증 방식) 사용 X
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않으므로 STATELESS로 설정
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers(
                        "/",
                        "/auth",
                        "/users",
                        "/users/auth/**",
                        "/users/password",
                        "/oauth2/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/users/follows/followings"
                        ,"/users/follows/followers",
                        "/swagger-ui.html",
                        "/markings/search",
                        "/health",
                        "/markings/image/**"
                    ).permitAll()

                    .requestMatchers(
                        "/users/nickname", "/users/additional-info",
                        "/addresses", "/addresses/**", "/users/me", "/users/profile/password", "/logout"
                    ).hasAnyRole(
                        Role.NONE.name(), Role.GUEST.name(), Role.USER.name(), Role.ADMIN.name()
                    )


                    .requestMatchers("/profile","/users/profile", "/users/profile/**")
                    .hasAnyRole(
                            Role.GUEST.name(), Role.USER.name(), Role.ADMIN.name()
                    )


                    .requestMatchers(HttpMethod.POST, "/pets")
                    .hasAnyRole(
                            Role.GUEST.name(), Role.USER.name(), Role.ADMIN.name()
                    )


                    .requestMatchers(
                        "/markings", "/maps/**", "/users/follows",
                        "/users/follows/**", "/markings/**"
                    ).hasAnyRole(
                        Role.USER.name(), Role.ADMIN.name()
                    )


                    .requestMatchers(HttpMethod.GET, "/pets")
                    .hasAnyRole(
                            Role.USER.name(), Role.ADMIN.name()
                    )


                    .requestMatchers(HttpMethod.PUT, "/pets")
                    .hasAnyRole(
                            Role.USER.name(), Role.ADMIN.name()
                    )

                    .anyRequest()
                    .authenticated())
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(customLogoutHandler)  // 커스텀 핸들러 추가
                .logoutSuccessHandler(customLogoutSuccessHandler) // 커스텀 성공 핸들러 추가
            );

        //oauth2
        http
            .oauth2Login((oauth2) -> oauth2
                .successHandler(customOAuthAuthenticationSuccessHandler(userService))
                .failureHandler(customOAuthAuthenticationFailureHandler)
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                    .userService(customOAuth2UserService(userService))))
        ;

        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(),
            LogoutFilter.class);                              // json 로그인 필터
        http.addFilterBefore(jwtAuthenticationProcessingFilter(),
            CustomJsonUsernamePasswordAuthenticationFilter.class);        // jwt 인증/인가 필터

        return http.build();
    }

    /**
     * PasswordEncoder를 자동으로 주입하기 위해 빈으로 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정 후 등록
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // PasswordEncoder를 사용
        provider.setUserDetailsService(userDetailsServiceImpl);   // UserDetailsService는 커스텀 LoginService로 등록
        return new ProviderManager(provider);
    }

    /**
     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
     */
    @Bean
    public CustomJsonAuthenticationSuccessHandler loginSuccessHandler() {
        return new CustomJsonAuthenticationSuccessHandler(jwtService, userRepository, baseResponse);
    }


    /**
     * userService 순환참조 문제로 인해
     * bean 으로 생성
     *
     * @param userService
     * @return
     */
    @Bean
    public CustomOAuthAuthenticationSuccessHandler customOAuthAuthenticationSuccessHandler(UserService userService) {
        return new CustomOAuthAuthenticationSuccessHandler(jwtService, userService, authorizedClientService);
    }

    /**
     * userService 순환참조 문제로 인해
     * bean 으로 생성
     *
     * @param userService
     * @return
     */
    @Bean
    public CustomOAuth2UserService customOAuth2UserService(UserService userService) {
        return new CustomOAuth2UserService(userService);
    }

    /**
     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
     */
    @Bean
    public CustomJsonAuthenticationFailureHandler loginFailureHandler() {
        return new CustomJsonAuthenticationFailureHandler(baseResponse);
    }

    /**
     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
     */
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
            = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper, userRepository);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(
            authenticationManager());        // 위에서 등록한 AuthenticationManager(ProviderManager) 설정
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(
            loginSuccessHandler());   // 로그인 성공 시 호출할 handler
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(
            loginFailureHandler());   // 실패 시 호출할 handler
        return customJsonUsernamePasswordLoginFilter;
    }

    /**
     * JwtAuthenticationProcessingFilter 빈 등록
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationFilter(jwtService);
    }
}
