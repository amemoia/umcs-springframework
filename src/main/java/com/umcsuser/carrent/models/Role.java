package com.umcsuser.carrent.models;

public enum Role {
    ADMIN, USER;

    public static Role fromString(String role) {
        if (role == null) return USER;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}

