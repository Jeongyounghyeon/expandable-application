package com.study.expandable_application_auth.service;

import com.study.expandable_application_auth.exception.ExceptionCode;
import com.study.expandable_application_auth.exception.ExpandableException;
import com.study.expandable_application_auth.model.dto.JwtTokenDto;
import com.study.expandable_application_auth.model.entity.AuthenticationDetailsEntity;
import com.study.expandable_application_auth.model.entity.RefreshTokenEntity;
import com.study.expandable_application_auth.repository.AuthenticationDetailsEntityRepository;
import com.study.expandable_application_auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = AuthenticationService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthenticationServiceTest {

    private final AuthenticationService authenticationService;

    @MockitoBean
    private final AuthenticationDetailsEntityRepository authenticationDetailsEntityRepository;

    @MockitoBean
    private final RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private final PasswordEncoder passwordEncoder;

    @MockitoBean
    private final JwtTokenService jwtTokenService;

    @Test
    public void createAuthentication() {
        final String id = "id";
        final String password = "password";
        final Long userId = 1L;

        given(authenticationDetailsEntityRepository.existsById(id)).willReturn(false);
        given(authenticationDetailsEntityRepository.save(Mockito.any())).willReturn(mock(AuthenticationDetailsEntity.class));

        authenticationService.createAuthentication(id, password, userId);
        Mockito.verify(authenticationDetailsEntityRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void createAuthentication_존재하는_아이디_테스트() {
        final String id = "id";
        final String password = "password";
        final Long userId = 1L;

        given(authenticationDetailsEntityRepository.existsById(id)).willReturn(true);

        ExceptionCode existIdExceptionCode = assertThrows(
                ExpandableException.class,
                () -> authenticationService.createAuthentication(id, password, userId)
        ).getCode();

        assertEquals(ExceptionCode.DUPLICATED_ID, existIdExceptionCode);
    }

    @Test
    public void authenticate() {
        final String id = "id";
        final String password = "password";
        final Long userId = 1L;

        // 1. user id 존재 여부 확인
        AuthenticationDetailsEntity authenticationDetailsEntity = mock(AuthenticationDetailsEntity.class);
        given(authenticationDetailsEntityRepository.findById(id)).willReturn(Optional.of(authenticationDetailsEntity));

        // 2. 비밀번호 일치 여부 확인
        given(passwordEncoder.matches(password, authenticationDetailsEntity.getPassword())).willReturn(true);

        // 3. JWT 토큰 생성
        String accessTokenValue = "accessToken";
        String refreshTokenValue = "refreshToken";
        JwtTokenDto jwtTokenDto = JwtTokenDto.of(accessTokenValue, refreshTokenValue);
        given(authenticationDetailsEntity.getUserId()).willReturn(userId);
        given(jwtTokenService.generateToken(eq(userId))).willReturn(jwtTokenDto);

        // 4. Refresh Token 저장
        given(jwtTokenService.getUserId(refreshTokenValue)).willReturn(userId);
        given(jwtTokenService.getExpiration(refreshTokenValue)).willReturn(mock(LocalDateTime.class));

        assertThat(authenticationService.authenticate(id, password))
                .isNotNull()
                .isEqualTo(jwtTokenDto);
    }

    @Test
    public void authenticate_실패_테스트_존재하지_않는_user_id() {
        final String id = "id";
        final String password = "password";

        // 1. user id 존재 여부 확인
        AuthenticationDetailsEntity authenticationDetailsEntity = mock(AuthenticationDetailsEntity.class);
        given(authenticationDetailsEntityRepository.findById(id)).willReturn(Optional.empty());

        ExceptionCode invalidIdExceptionCode = assertThrows(
                ExpandableException.class,
                () -> authenticationService.authenticate(id, password)
        ).getCode();

        assertEquals(ExceptionCode.INVALID_ID, invalidIdExceptionCode);
    }

    @Test
    public void authenticate_실패_테스트_일치하지_않는_비밀번호() {
        final String id = "id";
        final String password = "password";
        final Long userId = 1L;

        // 1. user id 존재 여부 확인
        AuthenticationDetailsEntity authenticationDetailsEntity = mock(AuthenticationDetailsEntity.class);
        given(authenticationDetailsEntityRepository.findById(id)).willReturn(Optional.of(authenticationDetailsEntity));

        // 2. 비밀번호 일치 여부 확인
        given(passwordEncoder.matches(password, authenticationDetailsEntity.getPassword())).willReturn(false);

        ExceptionCode invalidIdExceptionCode = assertThrows(
                ExpandableException.class,
                () -> authenticationService.authenticate(id, password)
        ).getCode();

        assertEquals(ExceptionCode.INVALID_PASSWORD, invalidIdExceptionCode);
    }

    @Test
    public void authenticate_By_AccessToken() {
        final String accessToken = "accessToken";
        willDoNothing().given(jwtTokenService).validateToken(accessToken);

        assertDoesNotThrow(() -> authenticationService.access(accessToken));
    }

    @Test
    public void refreshByRefreshToken() {
        // 1. Refresh Token 유효성 확인
        String refreshToken = "refreshToken";
        Long userId = 1L;
        LocalDateTime expiration = mock(LocalDateTime.class);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.of(refreshToken, userId, expiration);

        given(refreshTokenRepository.findByTokenValue(refreshToken)).willReturn(Optional.of(refreshTokenEntity));
        willDoNothing().given(jwtTokenService).validateToken(refreshToken);

        // 2. JWT 토큰 재생성
        String accessToken = "accessToken";
        String newRefreshTokenValue = "newRefreshToken";
        JwtTokenDto jwtTokenDto = JwtTokenDto.of(accessToken, newRefreshTokenValue);
        given(jwtTokenService.generateToken(userId)).willReturn(jwtTokenDto);

        // 3. Refresh Token 갱신
        given(jwtTokenService.getExpiration(newRefreshTokenValue)).willReturn(mock(LocalDateTime.class));

        assertThat(authenticationService.refresh(refreshToken))
                .isNotNull()
                .isEqualTo(jwtTokenDto);
    }

    @Test
    public void refreshByRefreshToken_실패_테스트_없는_토큰() {
        // 1. Refresh Token 유효성 확인
        String refreshToken = "refreshToken";

        given(refreshTokenRepository.findById(refreshToken)).willReturn(Optional.empty());

        ExceptionCode notExistExceptionCode = assertThrows(
                ExpandableException.class,
                () -> authenticationService.refresh(refreshToken)
        ).getCode();

        assertEquals(ExceptionCode.INVALID_TOKEN, notExistExceptionCode);
    }
}
