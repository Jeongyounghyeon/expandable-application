package com.study.app.service;

import com.study.app.client.AuthenticationClient;
import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.request.AuthenticationCreateRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(classes = AuthenticationService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthenticationServiceTest {

    private final AuthenticationService authenticationService;

    @MockitoBean
    private final AuthenticationClient authenticationClient;

    @Test
    public void authenticate() {
        String accessToken = "accessToken";

        given(authenticationClient.authenticate(eq("Bearer accessToken")))
                .willReturn(1L);

        Long userId = authenticationService.authenticate(accessToken);
        assertEquals(userId, 1L);
    }

    @Test
    public void authenticate_실패_테스트() {
        String accessToken = "accessToken";

        given(authenticationClient.authenticate(eq("Bearer accessToken")))
                .willThrow(FeignException.Unauthorized.class);

        ExpandableException e = assertThrows(ExpandableException.class, () -> authenticationService.authenticate(accessToken));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_TOKEN);
    }

    @Test
    public void createAuthentication() {
        Long userId = 1L;
        String id = "id";
        String password = "password";

        assertDoesNotThrow(() -> authenticationService.createAuthentication(userId, id, password));
    }

    @Test
    public void createAuthentication_실패_테스트() {
        Long userId = 1L;
        String id = "id";
        String password = "password";

        AuthenticationCreateRequest request = AuthenticationCreateRequest.of(id, password, userId);

        doThrow(FeignException.class)
                .when(authenticationClient)
                .createAuthentication(request);

        ExpandableException e = assertThrows(ExpandableException.class, () -> authenticationService.createAuthentication(userId, id, password));
        assertEquals(e.getCode(), ExceptionCode.AUTHENTICATION_CREATE_FAILED);
    }
}
