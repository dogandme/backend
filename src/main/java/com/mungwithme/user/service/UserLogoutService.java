package com.mungwithme.user.service;


import com.auth0.jwt.interfaces.Claim;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.login.service.LoginStatusService;
import com.mungwithme.security.jwt.service.JwtService;
import com.mungwithme.user.model.entity.User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserLogoutService {

    private final UserQueryService userQueryService;
    private final LoginStatusService loginStatusService;
    private final JwtService jwtService;

    /**
     * 로그아웃 API
     *
     * refreshToken 으로 유저 검색 후 null 처리
     */
    @Transactional
    public void logout(String refreshToken,String sessionId) {

        Map<String, Claim> jwtClaim = jwtService.getJwtClaim(refreshToken);

        String email = jwtClaim.get(JwtService.EMAIL_CLAIM).asString();
        User user = userQueryService.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("error.notfound.user"));

        loginStatusService.logoutStatus(user, sessionId);

    }
}
