package com.pick_me.backend.security;

import org.springframework.stereotype.Service;

@Service
public interface EmailVerificationService {
    int generateAndSendVerificationCode(String email);

    boolean verifyCode(String email, int code);

    int generateAndSendRecoveryCode(String email, String login);

    boolean verifyRecoveryCode(String code, String login);

    void deleteRecoveryCode(String code);
}
