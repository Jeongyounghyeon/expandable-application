package com.study.app.service;

import com.study.app.client.AuthenticationClient;
import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final AuthenticationClient authenticationClient;

    public Long authenticate(String accessToken) {
        String bearerToken = BEARER_TOKEN_PREFIX + accessToken;
        try {
            return authenticationClient.authenticate(bearerToken);
        } catch (FeignException.Unauthorized e) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_TOKEN);
        }
    }
}
