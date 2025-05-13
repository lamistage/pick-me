package com.pick_me.backend.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecoverPasswordRequest {
//    @NotBlank(message = "Login is required")
//    private String login;
    @NotBlank(message = "Email is required")
    private String email;
}
