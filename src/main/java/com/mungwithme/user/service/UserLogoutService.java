package com.mungwithme.user.service;


import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.user.model.entity.User;
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

    /**
     * 로그아웃 API
     *
     * refreshToken 으로 유저 검색 후 null 처리
     */
    @Transactional
    public void logout(String refreshToken) {
        User user = userQueryService.findByRefreshToken(refreshToken).orElseThrow(() -> new ResourceNotFoundException(
            "error.notfound.user"));
        // refreshToken으로 User 조회
        user.updateRefreshToken(null);
    }
}
