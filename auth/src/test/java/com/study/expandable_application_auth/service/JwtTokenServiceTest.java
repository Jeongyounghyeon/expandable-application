package com.study.expandable_application_auth.service;

import com.study.expandable_application_auth.exception.ExceptionCode;
import com.study.expandable_application_auth.exception.ExpandableException;
import com.study.expandable_application_auth.model.dto.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = JwtTokenService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JwtTokenServiceTest {

    private final JwtTokenService jwtTokenService;

    @Value("${jwt.access-token.expired-time-sec}")
    private Long accessTokenExpiredTimeSec;

    @Value("${jwt.refresh-token.expired-time-sec}")
    private Long refreshTokenExpiredTimeSec;

    @Test
    public void generateToken_생성_및_추출_테스트() {
        // given
        Long userId = 1L;

        // when
        final LocalDateTime timeBeforeGenerateToken = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final JwtTokenDto jwtTokenDto = jwtTokenService.generateToken(userId);
        final LocalDateTime timeAfterGenerateToken = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        // then
        // 토큰에 저장된 userId가 일치하는지 확인
        String accessToken = jwtTokenDto.getAccessToken();
        String refreshToken = jwtTokenDto.getRefreshToken();

        Long extractedUserIdFromAccessToken = jwtTokenService.getUserId(accessToken);
        Long extractedUserIdFromRefreshToken = jwtTokenService.getUserId(refreshToken);

        assertEquals(extractedUserIdFromAccessToken, userId);
        assertEquals(extractedUserIdFromRefreshToken, userId);

        // 토큰의 만료시간이 유효한지 확인
        LocalDateTime accessTokenExpiration = jwtTokenService.getExpiration(accessToken);
        LocalDateTime refreshTokenExpiration = jwtTokenService.getExpiration(refreshToken);

        LocalDateTime beforeAccessTokenExpiration = timeBeforeGenerateToken.plusSeconds(accessTokenExpiredTimeSec);
        LocalDateTime afterAccessTokenExpiration = timeAfterGenerateToken.plusSeconds(accessTokenExpiredTimeSec);
        LocalDateTime beforeRefreshTokenExpiration = timeBeforeGenerateToken.plusSeconds(refreshTokenExpiredTimeSec);
        LocalDateTime afterRefreshTokenExpiration = timeAfterGenerateToken.plusSeconds(refreshTokenExpiredTimeSec);

        assertThat(accessTokenExpiration).isAfterOrEqualTo(beforeAccessTokenExpiration);
        assertThat(accessTokenExpiration).isBeforeOrEqualTo(afterAccessTokenExpiration);
        assertThat(refreshTokenExpiration).isAfterOrEqualTo(beforeRefreshTokenExpiration);
        assertThat(refreshTokenExpiration).isBeforeOrEqualTo(afterRefreshTokenExpiration);
    }

    @Test
    public void validateToken() {
        Long userId = 1L;

        JwtTokenDto jwtTokenDto = jwtTokenService.generateToken(userId);
        String accessToken = jwtTokenDto.getAccessToken();
        String refreshToken = jwtTokenDto.getRefreshToken();

        Assertions.assertDoesNotThrow(() -> jwtTokenService.validateToken(accessToken));
        Assertions.assertDoesNotThrow(() -> jwtTokenService.validateToken(refreshToken));
    }

    @Test
    public void validateToken_실패_테스트_INVALID_TOKEN() {
        Long userId = 1L;

        JwtTokenDto jwtTokenDto = jwtTokenService.generateToken(userId);
        String invalidAccessToken = jwtTokenDto.getAccessToken() + "invalid";
        String invalidRefreshToken = jwtTokenDto.getRefreshToken() + "invalid";

        ExceptionCode accessTokenExceptionCode = assertThrows(
                ExpandableException.class,
                () -> jwtTokenService.validateToken(invalidAccessToken)
        ).getCode();

        ExceptionCode refreshTokenExceptionCode = assertThrows(
                ExpandableException.class,
                () -> jwtTokenService.validateToken(invalidRefreshToken)
        ).getCode();

        assertEquals(accessTokenExceptionCode, ExceptionCode.INVALID_TOKEN);
        assertEquals(refreshTokenExceptionCode, ExceptionCode.INVALID_TOKEN);
    }

    private final JwtTokenService expirationJwtTokenGenerationService = new JwtTokenService(
            "jwt-secret-key-for-development-test-environment-must-over-256-bit",
            -1L,
            -1L
    );

    @Test
    public void validateToken_실패_테스트_EXPIRED_TOKEN() {
        Long userId = 1L;

        JwtTokenDto jwtTokenDto = expirationJwtTokenGenerationService.generateToken(userId);
        String expiredAccessToken = jwtTokenDto.getAccessToken();
        String expiredRefreshToken = jwtTokenDto.getRefreshToken();

        ExceptionCode accessTokenExceptionCode = assertThrows(
                ExpandableException.class,
                () -> jwtTokenService.validateToken(expiredAccessToken)
        ).getCode();

        ExceptionCode refreshTokenExceptionCode =
                assertThrows(ExpandableException.class,
                () -> jwtTokenService.validateToken(expiredRefreshToken)
        ).getCode();

        assertEquals(accessTokenExceptionCode, ExceptionCode.EXPIRED_TOKEN);
        assertEquals(refreshTokenExceptionCode, ExceptionCode.EXPIRED_TOKEN);
    }
}
