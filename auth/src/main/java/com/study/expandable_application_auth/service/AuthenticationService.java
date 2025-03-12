package com.study.expandable_application_auth.service;

import com.study.expandable_application_auth.exception.ExceptionCode;
import com.study.expandable_application_auth.exception.ExpandableException;
import com.study.expandable_application_auth.model.entity.AuthenticationDetailsEntity;
import com.study.expandable_application_auth.repository.AuthenticationDetailsEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AuthenticationService {

    private final AuthenticationDetailsEntityRepository authenticationDetailsEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean isExistId(String id) {
        return authenticationDetailsEntityRepository.existsById(id);
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
        authenticationDetailsEntityRepository.save(authenticationDetailsEntity);

        log.info("Authentication created: id: {}, userId: {}", id, userId);
    }
}
