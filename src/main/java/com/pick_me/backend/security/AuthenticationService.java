package com.pick_me.backend.security;

import com.pick_me.backend.dto.LoginUserDTO;
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

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUp(LoginUserDTO input) {
        if (userRepository.findByLogin(input.getLogin()) != null) {
            throw new UserAlreadyExistsException("User with login " + input.getLogin() + " already exists");
        }

        User user = User.builder()
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
}
