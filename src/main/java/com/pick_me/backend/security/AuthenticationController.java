package com.pick_me.backend.security;

import com.pick_me.backend.dto.LoginUserDTO;
import com.pick_me.backend.security.exceptions.InvalidCredentialsException;
import com.pick_me.backend.security.exceptions.UserAlreadyExistsException;
import com.pick_me.backend.security.exceptions.UserNotFoundException;
import com.pick_me.backend.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            RefreshTokenService refreshTokenService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> register(@RequestBody LoginUserDTO loginUserDTO) {
        User registeredUser = authenticationService.signUp(loginUserDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDTO loginUserDTO) {
        User authenticatedUser = authenticationService.signIn(loginUserDTO);

        String accessToken = jwtService.generateAccessToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(new LoginResponse());
        }

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshToken);
        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse());
        }

        RefreshToken token = refreshTokenOpt.get();
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                jwtService.extractUsername(refreshToken, true)
        );

        if (!refreshTokenService.isRefreshTokenValid(refreshToken, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse());
        }

        String newAccessToken = jwtService.generateAccessToken((User) userDetails);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }

        refreshTokenService.revokeRefreshToken(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        authenticationService.changePassword(login, request);

        return ResponseEntity.ok("Password changed successfully");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<?> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}