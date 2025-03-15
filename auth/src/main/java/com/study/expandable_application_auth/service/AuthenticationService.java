package com.study.expandable_application_auth.service;

import com.study.expandable_application_auth.exception.ExceptionCode;
import com.study.expandable_application_auth.exception.ExpandableException;
import com.study.expandable_application_auth.model.dto.JwtTokenDto;
import com.study.expandable_application_auth.model.entity.AuthenticationDetailsEntity;
import com.study.expandable_application_auth.model.entity.RefreshTokenEntity;
import com.study.expandable_application_auth.repository.AuthenticationDetailsRepository;
import com.study.expandable_application_auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AuthenticationService {

    private final AuthenticationDetailsRepository authenticationDetailsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Transactional(readOnly = true)
    public boolean isExistId(String id) {
        return authenticationDetailsRepository.existsById(id);
    }

    public void createAuthentication(String id, String password, Long userId) {
        // 1. id 중복 확인
        if (isExistId(id)) {
            throw new ExpandableException(ExceptionCode.DUPLICATED_ID);
        }

        // 2. 비밀번호 암호화
        final String encodedPassword = passwordEncoder.encode(password);
        AuthenticationDetailsEntity authenticationDetailsEntity = AuthenticationDetailsEntity.of(id, encodedPassword, userId);

        // 3. 인증 정보 저장
        authenticationDetailsRepository.save(authenticationDetailsEntity);

        log.info("Authentication created: id: {}, userId: {}", id, userId);
    }

    public JwtTokenDto authenticate(String id, String password) {
        // 1. user id 존재 여부 확인
        AuthenticationDetailsEntity authenticationDetailsEntity = authenticationDetailsRepository.findById(id)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.INVALID_ID));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, authenticationDetailsEntity.getPassword())) {
            throw new ExpandableException(ExceptionCode.INVALID_PASSWORD);
        }

        // 3. JWT 토큰 생성
        JwtTokenDto jwtTokenDto = jwtTokenService.generateToken(authenticationDetailsEntity.getUserId());

        // 4. Refresh Token 저장
        String refreshTokenValue = jwtTokenDto.getRefreshToken();
        Long userId = jwtTokenService.getUserId(refreshTokenValue);
        LocalDateTime expiration = jwtTokenService.getExpiration(refreshTokenValue);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.of(refreshTokenValue, userId, expiration);

        refreshTokenRepository.save(refreshTokenEntity);

        return jwtTokenDto;
    }

    public Long access(String accessToken) {
        jwtTokenService.validateToken(accessToken);

        return jwtTokenService.getUserId(accessToken);
    }

    public JwtTokenDto refresh(String refreshToken) {
        // 1. Refresh Token 유효성 확인
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByTokenValue(refreshToken)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.INVALID_TOKEN));

        jwtTokenService.validateToken(refreshToken);

        // 2. JWT 토큰 재생성
        JwtTokenDto jwtTokenDto = jwtTokenService.generateToken(refreshTokenEntity.getUserId());

        // 3. Refresh Token 갱신
        String newRefreshTokenValue = jwtTokenDto.getRefreshToken();
        LocalDateTime newRefreshTokenExpiration = jwtTokenService.getExpiration(newRefreshTokenValue);
        refreshTokenEntity.update(newRefreshTokenValue, newRefreshTokenExpiration);

        refreshTokenRepository.save(refreshTokenEntity);

        return jwtTokenDto;
    }
}
