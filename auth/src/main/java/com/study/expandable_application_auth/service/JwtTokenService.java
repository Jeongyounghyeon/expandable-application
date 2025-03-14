package com.study.expandable_application_auth.service;

import com.study.expandable_application_auth.exception.ExceptionCode;
import com.study.expandable_application_auth.exception.ExpandableException;
import com.study.expandable_application_auth.model.dto.JwtTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtTokenService {

    private static final String USER_ID_KEY = "user_id";

    private final SecretKey secretKey;
    private final Long accessTokenExpiredTimeSec;
    private final Long refreshTokenExpiredTimeSec;

    public JwtTokenService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token.expired-time-sec}") Long accessTokenExpiredTimeSec,
            @Value("${jwt.refresh-token.expired-time-sec}") Long refreshTokenExpiredTimeSec
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiredTimeSec = accessTokenExpiredTimeSec;
        this.refreshTokenExpiredTimeSec = refreshTokenExpiredTimeSec;
    }

    public JwtTokenDto generateToken(Long userId) {
        String accessToken = generateToken(userId, accessTokenExpiredTimeSec);
        String refreshToken = generateToken(userId, refreshTokenExpiredTimeSec);

        return JwtTokenDto.of(accessToken, refreshToken);
    }

    public void validateToken(String token) {
        try {
            extractClaims(token);
        } catch (ExpiredJwtException e) {
            throw new ExpandableException(ExceptionCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new ExpandableException(ExceptionCode.INVALID_TOKEN, e);
        }
    }

    public Long getUserId(String token) {
        return extractClaims(token).get(USER_ID_KEY, Long.class);
    }

    public LocalDateTime getExpiration(String token) {
        return dateToLocalDateTime(extractClaims(token).getExpiration());
    }

    private String generateToken(Long userId, Long expiredTimeSec) {
        Claims claims = Jwts.claims()
                .add(USER_ID_KEY, userId)
                .build();

        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + (expiredTimeSec * 1000)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims extractClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
