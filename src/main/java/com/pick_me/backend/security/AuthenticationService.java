package com.pick_me.backend.security;

import com.pick_me.backend.dto.LoginUserDTO;
import com.pick_me.backend.security.exceptions.IncorrectPasswordException;
import com.pick_me.backend.security.exceptions.InvalidCredentialsException;
import com.pick_me.backend.security.exceptions.UserAlreadyExistsException;
import com.pick_me.backend.security.exceptions.UserNotFoundException;
import com.pick_me.backend.user.entity.User;
import com.pick_me.backend.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
    }

    public User signUp(LoginUserDTO input) {
        if (input.getVerificationCode() == null) {
            throw new IllegalArgumentException("Verification code is required");
        }

        boolean isCodeValid = emailVerificationService.verifyCode(input.getEmail(), input.getVerificationCode());
        if (!isCodeValid) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (userRepository.findByLogin(input.getLogin()) != null) {
            throw new UserAlreadyExistsException("User with login " + input.getLogin() + " already exists");
        }


        if (userRepository.findByEmail(input.getEmail()) != null) {
            throw new UserAlreadyExistsException("User with email " + input.getEmail() + " already exists");
        }

        User user = User.builder()
                .email(input.getEmail())
                .login(input.getLogin())
                .password(passwordEncoder.encode(input.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public User signIn(LoginUserDTO input) {
        User user = userRepository.findByLogin(input.getLogin());
        if (user == null) {
            throw new UserNotFoundException("User with login " + input.getLogin() + " not found");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getLogin(),
                            input.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof UsernameNotFoundException) {
                throw new UserNotFoundException("User with login " + input.getLogin() + " not found");
            } else {
                throw new InvalidCredentialsException("Invalid password for user " + input.getLogin());
            }
        }

        return user;
    }

    public void changePassword(String login, ChangePasswordRequest request) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException("User with login " + login + " not found");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void recoverPassword(RecoverPasswordRequest request) {
        User user = userRepository.findByLogin(request.getLogin());
        if (user == null) {
            throw new UserNotFoundException("User with login " + request.getLogin() + " not found");
        }

        emailVerificationService.generateAndSendRecoveryCode(user.getEmail(), request.getLogin());
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByLogin(request.getLogin());
        if (user == null) {
            throw new UserNotFoundException("User with login " + request.getLogin() + " not found");
        }

        boolean isCodeValid = emailVerificationService.verifyRecoveryCode(request.getCode(), request.getLogin());
        if (!isCodeValid) {
            throw new IllegalArgumentException("Invalid or expired recovery code");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
