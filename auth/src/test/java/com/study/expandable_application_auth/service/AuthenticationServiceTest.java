package com.study.expandable_application_auth.service;

import com.study.expandable_application_auth.model.entity.AuthenticationDetailsEntity;
import com.study.expandable_application_auth.repository.AuthenticationDetailsEntityRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = AuthenticationService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthenticationServiceTest {

    private final AuthenticationService authenticationService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationDetailsEntityRepository authenticationDetailsEntityRepository;

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
}
