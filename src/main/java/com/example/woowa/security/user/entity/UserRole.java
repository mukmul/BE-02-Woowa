package com.example.woowa.security.user.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {

    ROLE_CUSTOMER("CUSTOMER"),
    ROLE_RIDER("RIDER"),
    ROLE_OWNER("OWNER"),
    ROLE_ADMIN("ADMIN");

    private final String roleName;

    public String getRoleName() {
        return roleName;
    }
}
