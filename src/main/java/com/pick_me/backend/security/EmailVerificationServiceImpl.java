package com.pick_me.backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final String fromEmail;
    private final long ttlMinutes;
    private final String frontendUrl;

    public EmailVerificationServiceImpl(
            JavaMailSender javaMailSender,
            RedisTemplate<String, Integer> redisTemplate,
            @Value("${spring.mail.username}") String fromEmail,
            @Value("${spring.verification-code.ttl-minutes}") long ttlMinutes,
            @Value("${frontend.url}") String frontendUrl
    ) {
        this.javaMailSender = javaMailSender;
        this.redisTemplate = redisTemplate;
        this.fromEmail = fromEmail;
        this.ttlMinutes = ttlMinutes;
        this.frontendUrl = frontendUrl;

        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            System.out.println("Successfully connected to Redis");
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
        }
    }

    @Override
    public int generateAndSendVerificationCode(String email) {
        log.info("Generating and sending verification code for email={}", email);
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            log.warn("Email={} is not valid for verification", email);
            throw new IllegalArgumentException("Invalid email address: " + email);
        }

        log.info("Generating code for email={}", email);
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        log.info("Verification code generated for email={}", email);

        String redisKey = "email-verification:" + email;
        log.debug("Storing verification code in Redis: key={}, ttl={} minutes", redisKey, ttlMinutes);
        redisTemplate.opsForValue().set(redisKey, code, ttlMinutes, TimeUnit.MINUTES);

        try {
            log.info("Creating email message with verification code for email={}", email);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setReplyTo(fromEmail);
            helper.setTo(email);
            helper.setSubject("PickMe - Email Verification Code");
            helper.setText(
                    "<html>" +
                            "<body style='color: #000000;'>" +
                            "<h2>Hello from PickMe!</h2>" +
                            "<p>Thank you for signing up. Your verification code is:</p>" +
                            "<h3 style='color: #ff8fa3;'>" + code + "</h3>" +
                            "<p>If you didn’t request this code, please ignore this email.</p>" +
                            "<p>Best regards,<br>The PickMe Team</p>" +
                            "</body>" +
                            "</html>",
                    true
            );
            log.info("Sending email with verification code for email={}", email);
            javaMailSender.send(message);
            log.info("Email with verification code sent successfully for email={}", email);
        } catch (MailException | MessagingException e) {
            log.error("Error while sending email with verification code for email={}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }

        return code;
    }

    @Override
    public boolean verifyCode(String email, int code) {
        log.info("Verifying code={} for email={}", code, email);
        String redisKey = "email-verification:" + email;
        Integer storedCode = redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            log.warn("No verification code found in Redis for email={}", email);
            return false;
        }

        boolean isValid = (storedCode == code);
        log.info("Verification code for email={} is {}", email, isValid ? "valid" : "invalid");

        if (isValid) {
            redisTemplate.delete(redisKey);
            log.info("Deleted verification code for email={}", email);
        }
        return isValid;
    }

    @Override
    public int generateAndSendRecoveryCode(String email, String login) {
        log.info("Generating and sending recovery code for user with email={}", email);
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            log.warn("Email={} is not valid for recovery", email);
            throw new IllegalArgumentException("Invalid email address: " + email);
        }

        log.info("Generating recovery code for email={}", email);
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        log.info("Recovery code generated for email={}", email);

        String redisKey = "password-recovery:" + login;
        redisTemplate.opsForValue().set(redisKey, code, ttlMinutes, TimeUnit.MINUTES);

        try {
            log.info("Creating recovery email message for email={}", email);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setReplyTo(fromEmail);
            helper.setTo(email);
            helper.setSubject("PickMe - Password Recovery");
            helper.setText(
                    "<html>" +
                            "<body style='color: #000000;'>" +
                            "<h2>Hello from PickMe!</h2>" +
                            "<p>You requested to reset your password. Click the link below to set a new password:</p>" +
                            "<p><a href='" + frontendUrl + "/reset-password?code=" + code + "' style='color: #ff8fa3; text-decoration: none; font-weight: bold;'>Reset Password</a></p>" +
                            "<p>This link will expire in " + ttlMinutes + " minutes.</p>" +
                            "<p>If you didn’t request this, please ignore this email.</p>" +
                            "<p>Best regards,<br>The PickMe Team</p>" +
                            "</body>" +
                            "</html>",
                    true
            );
            log.info("Sending recovery email for email={}", email);
            javaMailSender.send(message);
            log.info("Recovery email sent successfully for email={}", email);
        } catch (MailException | MessagingException e) {
            log.error("Error while sending recovery email for email={}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send recovery email: " + e.getMessage(), e);
        }

        return code;
    }

    @Override
    public boolean verifyRecoveryCode(String code, String login) {
        log.info("Verify recovery code for user with login={}", login);
        String redisKey = "password-recovery:" + login;
        Integer storedCode = redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            log.warn("No recovery code found in Redis for login={}", login);
            return false;
        }

        try {
            int providedCode = Integer.parseInt(code);
            boolean isValid = (storedCode == providedCode);
            log.info("Recovery code for login={} is {}", login, isValid ? "valid" : "invalid");
            if (isValid) {
                redisTemplate.delete(redisKey);
                log.info("Deleted recovery code for login={}", login);
            }
            return isValid;
        } catch (NumberFormatException e) {
            log.warn("Provided recovery code is not a valid integer: {}", code);
            return false;
        }
    }

    @Override
    public void deleteRecoveryCode(String login) {
        log.info("Deleting recovery code for user with login={}", login);
        String redisKey = "password-recovery:" + login;
        redisTemplate.delete(redisKey);
        log.info("Deleted recovery code for user with login={}", login);
    }
}