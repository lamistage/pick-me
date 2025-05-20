package com.pick_me.backend.security;

import com.pick_me.backend.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refresh token for user with id={}", user.getId());
        String token = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000));
        refreshToken.setRevoked(false);
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created with id={} for user with id={}", savedToken, user.getId());
        return savedToken;
    }

    public Optional<RefreshToken> findByToken(String token) {
        log.info("Searching refresh token");
        return refreshTokenRepository.findByToken(token);
    }

    public void revokeRefreshToken(String token) {
        log.info("Revoking refresh token");
        Optional<RefreshToken> refreshTokenOpt = findByToken(token);
        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            log.info("Refresh token revoked: id={}, userId={}", refreshToken.getId(), refreshToken.getUserId());
        } else {
            log.warn("Attempted to revoke non-existent refresh token");
        }
    }

    public void deleteByUserId(Long userId) {
        log.info("Deleting all refresh tokens for userId={}", userId);
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Deleted refresh tokens for userId={}", userId);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        log.debug("Validating refresh token: {}", token);
        Optional<RefreshToken> refreshTokenOpt = findByToken(token);
        if (refreshTokenOpt.isEmpty()) {
            return false;
        }
        RefreshToken refreshToken = refreshTokenOpt.get();
        boolean valid = !refreshToken.isRevoked() &&
                !refreshToken.getExpiresAt().isBefore(LocalDateTime.now()) &&
                jwtService.isTokenValid(token, userDetails, true);
        log.debug("Refresh token validation result for token {}: {}", token, valid);
        return valid;
    }
}