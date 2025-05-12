package com.pick_me.backend.security;

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
            @Value("${frontend.url:http://localhost:4200}") String frontendUrl
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
            e.printStackTrace();
        }
    }

    @Override
    public int generateAndSendVerificationCode(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }

        System.out.println("Generating code for email: " + email);
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        System.out.println("Generated code: " + code);

        String redisKey = "email-verification:" + email;
        redisTemplate.opsForValue().set(redisKey, code, ttlMinutes, TimeUnit.MINUTES);

        try {
            System.out.println("Creating email message...");
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
            System.out.println("Sending email...");
            javaMailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (MailException | MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }

        return code;
    }

    @Override
    public boolean verifyCode(String email, int code) {
        String redisKey = "email-verification:" + email;
        Integer storedCode = redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            return false;
        }

        boolean isValid = (storedCode == code);
        if (isValid) {
            redisTemplate.delete(redisKey);
        }
        return isValid;
    }

    @Override
    public int generateAndSendRecoveryCode(String email, String login) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }

        System.out.println("Generating recovery code for email: " + email + ", login: " + login);
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        System.out.println("Generated recovery code: " + code);

        String redisKey = "password-recovery:" + login;
        redisTemplate.opsForValue().set(redisKey, code, ttlMinutes, TimeUnit.MINUTES);

        try {
            System.out.println("Creating recovery email message...");
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
                            "<p><a href='" + "http://localhost:4200/reset-password?code=" + code + "' style='color: #ff8fa3; text-decoration: none; font-weight: bold;'>Reset Password</a></p>" +
                            "<p>This link will expire in " + ttlMinutes + " minutes.</p>" +
                            "<p>If you didn’t request this, please ignore this email.</p>" +
                            "<p>Best regards,<br>The PickMe Team</p>" +
                            "</body>" +
                            "</html>",
                    true
            );
            System.out.println("Sending recovery email...");
            javaMailSender.send(message);
            System.out.println("Recovery email sent successfully");
        } catch (MailException | MessagingException e) {
            System.err.println("Recovery email sending failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send recovery email: " + e.getMessage(), e);
        }

        return code;
    }

    @Override
    public boolean verifyRecoveryCode(String code, String login) {
        String redisKey = "password-recovery:" + login;
        Integer storedCode = redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            return false;
        }

        try {
            int providedCode = Integer.parseInt(code);
            boolean isValid = (storedCode == providedCode);
            if (isValid) {
                redisTemplate.delete(redisKey);
            }
            return isValid;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void deleteRecoveryCode(String login) {
        String redisKey = "password-recovery:" + login;
        redisTemplate.delete(redisKey);
    }
}