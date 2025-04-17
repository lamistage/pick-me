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

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.verification-code.ttl-minutes}")
    private long ttlMinutes;

    public EmailVerificationServiceImpl(
            JavaMailSender javaMailSender,
            RedisTemplate<String, Integer> redisTemplate
    ) {
        this.javaMailSender = javaMailSender;
        this.redisTemplate = redisTemplate;

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
                            "<body>" +
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
}