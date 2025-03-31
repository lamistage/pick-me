package com.pick_me.backend.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Data
@SuperBuilder
@Component
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
}