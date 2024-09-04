package com.mungwithme.user.model;

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

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
