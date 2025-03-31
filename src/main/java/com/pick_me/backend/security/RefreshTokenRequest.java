package com.pick_me.backend.security;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}