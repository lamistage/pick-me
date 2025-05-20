package com.pick_me.backend.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "New password is required")
    private String newPassword;
}
