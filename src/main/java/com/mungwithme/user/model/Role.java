package com.mungwithme.user.model;

import javax.naming.AuthenticationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 권한 종류
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    NONE("ROLE_NONE"), GUEST("ROLE_GUEST"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

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
