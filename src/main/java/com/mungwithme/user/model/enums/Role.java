package com.mungwithme.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 권한 종류
 */
@Getter
@RequiredArgsConstructor
public enum Role {


    // 비회원인 경우 자동으로 ROLE_ANONYMOUS 추가
    ANONYMOUS("ROLE_ANONYMOUS"),NONE("ROLE_NONE"), GUEST("ROLE_GUEST"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String key;


    /**
     * Key값으로 권한 찾기
     * @param str
     * @return
     */
    public static Role findByStr(String str) {
        Role[] values = values();
        for (Role value : values) {
            if (value.key.equals(str)) {
                return value;
            }
        }
        return Role.NONE;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

}