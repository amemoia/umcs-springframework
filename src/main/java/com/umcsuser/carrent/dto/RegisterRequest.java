package com.umcsuser.carrent.dto;

public record RegisterRequest(
        String login,
        String password
) {
}