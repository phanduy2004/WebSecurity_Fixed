package com.group8.alomilktea.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    USER("USER"),
    GUEST("GUEST"),
    SHIPPER("SHIPPER");

    private final String roleName;

    private UserRole(String role) {
        this.roleName = role;
    }

    public static UserRole getEnum(String role) {
        for (UserRole v : values())
            if (v.getRoleName().equals(role)) return v;
        throw new IllegalArgumentException();
    }
}
