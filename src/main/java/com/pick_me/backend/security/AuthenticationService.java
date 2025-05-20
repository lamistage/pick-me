package com.pick_me.backend.security;

import com.pick_me.backend.dto.LoginUserDTO;
import com.pick_me.backend.security.exceptions.IncorrectPasswordException;
import com.pick_me.backend.security.exceptions.InvalidCredentialsException;
import com.pick_me.backend.security.exceptions.UserAlreadyExistsException;
import com.pick_me.backend.security.exceptions.UserNotFoundException;
import com.pick_me.backend.user.entity.User;
import com.pick_me.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    public void checkEmailAvailability(String email) {
        log.info("Checking email availability: {}", email);
        if (userRepository.findByEmail(email) != null) {
            log.warn("Email already exists: {}", email);
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        log.info("Email is available: {}", email);
    }

    public User signUp(LoginUserDTO input) {
        log.info("Sign up attempt: login={}, email={}", input.getLogin(), input.getEmail());

        if (input.getVerificationCode() == null) {
            log.warn("Verification code missing for email={}", input.getEmail());
            throw new IllegalArgumentException("Verification code is required");
        }

        boolean isCodeValid = emailVerificationService.verifyCode(input.getEmail(), input.getVerificationCode());
        if (!isCodeValid) {
            log.warn("Invalid verification code for email={}", input.getEmail());
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (userRepository.findByLogin(input.getLogin()) != null) {
            log.warn("User with login {} already exists", input.getLogin());
            throw new UserAlreadyExistsException("User with login " + input.getLogin() + " already exists");
        }


        if (userRepository.findByEmail(input.getEmail()) != null) {
            log.warn("User with email {} already exists", input.getEmail());
            throw new UserAlreadyExistsException("User with email " + input.getEmail() + " already exists");
        }

        User user = User.builder()
                .email(input.getEmail())
                .login(input.getLogin())
                .password(passwordEncoder.encode(input.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User signed up successfully: id={}, login={}", savedUser.getId(), savedUser.getLogin());
        return savedUser;
    }

    public User signIn(LoginUserDTO input) {
        log.info("Sign in attempt: login={}, email={}", input.getLogin(), input.getEmail());

        if ((input.getLogin() == null || input.getLogin().isBlank()) &&
                (input.getEmail() == null || input.getEmail().isBlank())) {
            log.warn("Neither login nor email provided for sign in");
            throw new IllegalArgumentException("Either login or email must be provided");
        }

        User user;
        if (input.getLogin() != null && !input.getLogin().isBlank()) {
            user = userRepository.findByLogin(input.getLogin());
            if (user == null) {
                throw new UserNotFoundException("User with login " + input.getLogin() + " not found");
            }
        } else {
            user = userRepository.findByEmail(input.getEmail());
            if (user == null) {
                log.warn("User with email {} not found", input.getEmail());
                throw new UserNotFoundException("User with email " + input.getEmail() + " not found");
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getLogin(),
                            input.getPassword()
                    )
            );
            log.info("User authenticated successfully: id={}, login={}", user.getId(), user.getLogin());
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UsernameNotFoundException) {
                log.warn("Authentication failed: user not found for login={}", user.getLogin());
                throw new UserNotFoundException("User not found");
            } else {
                log.warn("Authentication failed: invalid password for login={}", user.getLogin());
                throw new InvalidCredentialsException("Invalid password");
            }
        }

        return user;
    }

    public void changePassword(String login, ChangePasswordRequest request) {
        log.info("Change password attempt for login={}", login);
        User user = userRepository.findByLogin(login);
        if (user == null) {
            log.warn("User not found for login={}", login);
            throw new UserNotFoundException("User with login " + login + " not found");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Incorrect current password for login={}", login);
            throw new IncorrectPasswordException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("New password matches current password for login={}", login);
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for login={}", login);
    }

    public void recoverPassword(RecoverPasswordRequest request) {
        log.info("Password recovery requested for email={}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            log.warn("User not found for email={} during recovering password", request.getEmail());
            throw new UserNotFoundException("User with email " + request.getEmail() + " not found");
        }

        emailVerificationService.generateAndSendRecoveryCode(user.getEmail(), user.getLogin());
        log.info("Recovery code generated and sent for email={}", user.getEmail());
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.info("Password reset requested for email={}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            log.warn("User not found for email={} during resetting password", request.getEmail());
            throw new UserNotFoundException("User with email " + request.getEmail() + " not found");
        }

        boolean isCodeValid = emailVerificationService.verifyRecoveryCode(request.getCode(), user.getLogin());
        if (!isCodeValid) {
            log.warn("Invalid or expired recovery code for email={}", request.getEmail());
            throw new IllegalArgumentException("Invalid or expired recovery code");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset successfully for email={}", request.getEmail());
    }
}
