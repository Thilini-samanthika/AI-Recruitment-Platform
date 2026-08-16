package com.recruitment.auth.entity;

public enum RoleEnum {
    ROLE_CANDIDATE,
    ROLE_COMPANY,
    ROLE_ADMIN;

    public static RoleEnum fromString(String roleName) {
        if (roleName == null) {
            return ROLE_CANDIDATE;
        }
        String normalized = roleName.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        return RoleEnum.valueOf(normalized);
    }
}
