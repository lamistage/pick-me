package com.pick_me.backend.security;

import com.pick_me.backend.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String accessSecretKey;

    @Getter
    @Value("${security.jwt.expiration-time}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @Getter
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshTokenExpiration;

    public String extractUsername(String token, boolean isRefreshToken) {
        try {
            String username = extractClaim(token, Claims::getSubject, isRefreshToken);
            log.debug("Extracted username '{}' from {} token", username, isRefreshToken ? "refresh" : "access");
            return username;
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean isRefreshToken) {
        try {
            final Claims claims = extractAllClaims(token, isRefreshToken);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public String generateAccessToken(User user) {
        log.info("Generating access token for user with id={}", user.getId());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        return buildToken(extraClaims, user, accessTokenExpiration, false);
    }

    public String generateRefreshToken(User user) {
        log.info("Generating refresh token for user with id={}", user.getId());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        return buildToken(extraClaims, user, refreshTokenExpiration, true);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            boolean isRefreshToken
    ) {
        String token = Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(isRefreshToken), SignatureAlgorithm.HS256)
                .compact();
        log.debug("Built {} token for user '{}', expires in {} ms", isRefreshToken ? "refresh" : "access", userDetails.getUsername(), expiration);
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails, boolean isRefreshToken) {
        try {
            final String username = extractUsername(token, isRefreshToken);
            boolean valid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token, isRefreshToken);
            log.debug("{} token validation for user '{}': {}", isRefreshToken ? "Refresh" : "Access", username, valid);
            return valid;
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token, boolean isRefreshToken) {
        Date expiration = extractExpiration(token, isRefreshToken);
        boolean expired = expiration.before(new Date());
        if (expired) {
            log.debug("{} token expired at {}", isRefreshToken ? "Refresh" : "Access", expiration);
        }
        return expired;
    }

    private Date extractExpiration(String token, boolean isRefreshToken) {
        return extractClaim(token, Claims::getExpiration, isRefreshToken);
    }

    private Claims extractAllClaims(String token, boolean isRefreshToken) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey(isRefreshToken))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    private Key getSignInKey(boolean isRefreshToken) {
        byte[] keyBytes = Decoders.BASE64.decode(isRefreshToken ? refreshSecretKey : accessSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}